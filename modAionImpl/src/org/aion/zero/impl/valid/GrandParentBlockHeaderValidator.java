package org.aion.zero.impl.valid;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.aion.mcf.blockchain.BlockHeader;
import org.aion.mcf.blockchain.BlockHeader.Seal;
import org.slf4j.Logger;

public class GrandParentBlockHeaderValidator {

    private Map<Seal, List<GrandParentDependantBlockHeaderRule>> chainRules;

    public GrandParentBlockHeaderValidator(
        Map<Seal, List<GrandParentDependantBlockHeaderRule>> rules) {
        if (rules == null) {
            throw new NullPointerException();
        }
        chainRules = rules;
    }

    public boolean validate(
            BlockHeader parent, BlockHeader grandParent, BlockHeader current, Logger logger) {
        if (parent == null) {
            RuleError err = new RuleError(this.getClass(),"the input parent header is null");
            BlockHeaderValidatorUtil.logErrors(
                    logger, this.getClass().getSimpleName(), Collections.singletonList(err));
            return false;
        }

        if (current == null) {
            RuleError err = new RuleError(this.getClass(),"the input header is null");
            BlockHeaderValidatorUtil.logErrors(
                    logger, this.getClass().getSimpleName(), Collections.singletonList(err));
            return false;
        }

        List<GrandParentDependantBlockHeaderRule> rules = chainRules.get(current.getSealType());

        if (rules == null) {
            return false;
        } else {
            List<RuleError> errors = new LinkedList<>();
            for (GrandParentDependantBlockHeaderRule rule : rules) {
                if (!rule.validate(parent, grandParent, current, errors)) {
                    if (logger != null) {
                        BlockHeaderValidatorUtil.logErrors(
                                logger, this.getClass().getSimpleName(), errors);
                    }
                    return false;
                }
            }
        }

        return true;
    }
}