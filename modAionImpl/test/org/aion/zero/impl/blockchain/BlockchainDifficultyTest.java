package org.aion.zero.impl.blockchain;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;
import java.util.Collections;
import org.aion.zero.impl.core.ImportResult;
import org.aion.zero.impl.types.MiningBlock;
import org.junit.Test;

public class BlockchainDifficultyTest {
    @Test
    public void testDifficultyFirstBlock() {
        StandaloneBlockchain.Bundle bundle =
                new StandaloneBlockchain.Builder()
                        .withValidatorConfiguration("simple")
                        .withDefaultAccounts()
                        .build();

        MiningBlock firstBlock =
                bundle.bc.createNewMiningBlock(bundle.bc.getGenesis(), Collections.emptyList(), true);
        assertThat(firstBlock.getDifficultyBI())
                .isEqualTo(bundle.bc.getGenesis().getDifficultyBI());
        assertThat(bundle.bc.tryToConnect(firstBlock)).isEqualTo(ImportResult.IMPORTED_BEST);
    }

    // for all other blocks, we should not have a corner case
    @Test
    public void testDifficultyNotFirstBlock() {
        StandaloneBlockchain.Bundle bundle =
                new StandaloneBlockchain.Builder()
                        .withValidatorConfiguration("simple")
                        .withDefaultAccounts()
                        .build();

        MiningBlock firstBlock =
                bundle.bc.createNewMiningBlock(bundle.bc.getGenesis(), Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(firstBlock)).isEqualTo(ImportResult.IMPORTED_BEST);

        // connect second block
        MiningBlock secondBlock = bundle.bc.createNewMiningBlock(firstBlock, Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(secondBlock)).isEqualTo(ImportResult.IMPORTED_BEST);

        // due to us timestamping the genesis at 0
        assertThat(secondBlock.getDifficultyBI()).isLessThan(firstBlock.getDifficultyBI());
    }

    @Test
    public void testDifficultyThirdBlock() {
        StandaloneBlockchain.Bundle bundle =
                new StandaloneBlockchain.Builder()
                        .withValidatorConfiguration("simple")
                        .withDefaultAccounts()
                        .build();

        MiningBlock firstBlock =
                bundle.bc.createNewMiningBlock(bundle.bc.getGenesis(), Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(firstBlock)).isEqualTo(ImportResult.IMPORTED_BEST);

        // connect second block
        MiningBlock secondBlock = bundle.bc.createNewMiningBlock(firstBlock, Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(secondBlock)).isEqualTo(ImportResult.IMPORTED_BEST);

        // due to us timestamping the genesis at 0
        assertThat(secondBlock.getDifficultyBI()).isLessThan(firstBlock.getDifficultyBI());

        // connect second block
        MiningBlock thirdBlock = bundle.bc.createNewMiningBlock(secondBlock, Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(thirdBlock)).isEqualTo(ImportResult.IMPORTED_BEST);

        // due to us timestamping the genesis at 0
        assertThat(thirdBlock.getDifficultyBI()).isGreaterThan(secondBlock.getDifficultyBI());
    }

    @Test
    public void testDifficultyTenBlock() {
        StandaloneBlockchain.Bundle bundle =
                new StandaloneBlockchain.Builder()
                        .withValidatorConfiguration("simple")
                        .withDefaultAccounts()
                        .build();

        MiningBlock preBlock =
                bundle.bc.createNewMiningBlock(bundle.bc.getGenesis(), Collections.emptyList(), true);

        assertThat(bundle.bc.tryToConnect(preBlock)).isEqualTo(ImportResult.IMPORTED_BEST);
        BigInteger td = bundle.bc.getGenesis().getDifficultyBI().add(preBlock.getDifficultyBI());
        assertThat(td).isEqualTo(bundle.bc.getCacheTD());
        System.out.println(
                "new block: "
                        + preBlock.getNumber()
                        + " added! diff: "
                        + preBlock.getDifficultyBI().toString()
                        + " td: "
                        + td);

        assertThat(td).isEqualTo(bundle.bc.getTotalDifficulty());

        for (int i = 0; i < 10; i++) {
            MiningBlock newBlock = bundle.bc.createNewMiningBlock(preBlock, Collections.emptyList(), true);

            assertThat(bundle.bc.tryToConnect(newBlock)).isEqualTo(ImportResult.IMPORTED_BEST);
            td = td.add(newBlock.getDifficultyBI());
            assertThat(td).isEqualTo(bundle.bc.getCacheTD());
            System.out.println(
                    "new block: "
                            + newBlock.getNumber()
                            + " added! diff: "
                            + newBlock.getDifficultyBI().toString()
                            + " td: "
                            + td);

            if (i > 0) {
                assertThat(preBlock.getDifficultyBI()).isLessThan(newBlock.getDifficultyBI());
            }
            assertThat(bundle.bc.getTotalDifficulty()).isEqualTo(td);

            preBlock = newBlock;
        }
    }
}
