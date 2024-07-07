package com.jpabook.jpashop.config;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.RecoveryLog;
import com.atomikos.recovery.TxState;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class AtomikosRestPortController {
    public static final String REST_URL_PROPERTY_NAME = "com.atomikos.icatch.rest_port_url";
    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosRestPortController.class);
    private static String atomikosRestPortUrl;
    private static RecoveryLog recoveryLog;

    private final AtomikosRestPort atomikosRestPort;

    public AtomikosRestPortController(AtomikosRestPort atomikosRestPort) {
        this.atomikosRestPort = atomikosRestPort;
    }

    public static void setUrl(String url) {
        if (atomikosRestPortUrl == null && url != null) {
            atomikosRestPortUrl = url;
            if (!atomikosRestPortUrl.endsWith("/")) {
                atomikosRestPortUrl = atomikosRestPortUrl + "/";
            }
        }

    }

    public static String getUrl() {
        return atomikosRestPortUrl;
    }

    public static String buildParticipantUrl(CompositeTransaction ct) throws SysException {
        assertRestPortUrlSet();
        return getUrl() + ct.getCompositeCoordinator().getRootId() + "/" + ct.getCompositeCoordinator().getCoordinatorId();
    }

    private static void assertRestPortUrlSet() {
        if (getUrl() == null) {
            throw new SysException("Please set property com.atomikos.icatch.rest_port_url - see https://www.atomikos.com/Documentation/ConfiguringRemoting for details");
        }
    }

    public static void init(String url) {
        recoveryLog = Configuration.getRecoveryLog();
        setUrl(url);
    }

    private static String buildParticipantUrl(String root, String coordinatorId) {
        return atomikosRestPortUrl + root + "/" + coordinatorId;
    }

    @GetMapping(value = "/atomikos", consumes = "application/vnd.atomikos+json")
    public String ping() {
        return "Hello from Atomikos!";
    }

    @GetMapping(value = "/atomikos/{coordinatorId}", consumes = "application/vnd.atomikos+json")
    public String getOutcome(@PathVariable("coordinatorId") String coordinatorId) {
        TxState ret = TxState.TERMINATED;
        PendingTransactionRecord record = null;

        try {
            record = recoveryLog.get(coordinatorId);
        } catch (LogReadException var5) {
            LOGGER.logWarning("Unexpected log exception", var5);
            this.throw409(var5);
        }

        if (record != null) {
            ret = record.state;
        }

        return ret.toString();
    }

    @PostMapping(value = "/atomikos/{rootId}/{coordinatorId}", consumes = "application/vnd.atomikos+json")
    public ResponseEntity<Void> prepare(@PathVariable("rootId") String rootId, @PathVariable("coordinatorId") String coordinatorId, Map<String, Integer> cascadeList) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("prepare ( ... ) received for root " + rootId);
        }

        TransactionService service = Configuration.getTransactionService();
        String extentUri = buildParticipantUrl(rootId, coordinatorId);
        Integer count = (Integer)cascadeList.get(extentUri);
        Participant part = service.getParticipant(rootId);
        if (part == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            part.setGlobalSiblingCount(count);
            part.setCascadeList(cascadeList);
            int result = -1;

            try {
                result = part.prepare();
            } catch (RollbackException var10) {
                LOGGER.logWarning("Error in prepare for root " + rootId, var10);
                this.throw404();
            } catch (Exception var11) {
                LOGGER.logWarning("Error in prepare for root " + rootId, var11);
                this.throw409(var11);
            }

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    private void throw404() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction has timed out and was rolledback");
    }

    private void throw409(Exception e) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }

    @PutMapping(value = "{rootId}/{coordinatorId}/{onePhase}", consumes = "application/vnd.atomikos+json")
    public ResponseEntity<Void> commit(@PathVariable("rootId") String rootId, @PathVariable("coordinatorId") String coordinatorId, @PathVariable("onePhase") boolean onePhase) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("commit() received for root " + rootId + " onePhase = " + onePhase);
        }

        TransactionService service = Configuration.getTransactionService();
        CoordinatorImp part = (CoordinatorImp)service.getParticipant(rootId);
        if (part != null) {
            if (!part.getState().isFinalState()) {
                if (!part.getState().transitionAllowedTo(TxState.COMMITTING)) {
                    if (!onePhase) {
                        LOGGER.logWarning("Commit no longer allowed for root " + rootId + " - probably due to heuristic rollback?");
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                } else {
                    try {
                        part.commit(onePhase);
                    } catch (RollbackException var8) {
                        LOGGER.logWarning("Error in commit for root " + rootId, var8);
                        this.throw404();
                    } catch (Exception var9) {
                        LOGGER.logWarning("Error in commit for root " + rootId, var9);
                        this.throw409(var9);
                    }
                }
            }
        } else {
            if (onePhase) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "One-phase commit not allowed for root " + rootId);
            }

            try {
                this.delegateToRecovery(coordinatorId, true);
            } catch (LogException var7) {
                LOGGER.logWarning("Error in commit for root " + rootId, var7);
                this.throw409(var7);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void delegateToRecovery(String coordinatorId, boolean commit) throws LogException {
        if (recoveryLog == null) {
            recoveryLog = Configuration.getRecoveryLog();
        }

        if (commit) {
            recoveryLog.recordAsCommitting(coordinatorId);
        } else {
            recoveryLog.forget(coordinatorId);
        }

    }

    @DeleteMapping(value = "{rootId}/{coordinatorId}", consumes = "application/vnd.atomikos+json")
    public ResponseEntity<Void> rollback(@PathVariable("rootId") String rootId, @PathVariable("coordinatorId") String coordinatorId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug("rollback() received for root " + rootId);
        }

        TransactionService service = Configuration.getTransactionService();
        Participant part = service.getParticipant(rootId);
        if (part != null) {
            try {
                part.rollback();
            } catch (Exception var7) {
                LOGGER.logWarning("Error in rollback for root " + rootId, var7);
                this.throw409(var7);
            }
        } else {
            try {
                this.delegateToRecovery(coordinatorId, false);
            } catch (LogException var6) {
                LOGGER.logWarning("Error in rollback for root " + rootId, var6);
                this.throw409(var6);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
