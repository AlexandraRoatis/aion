package org.aion.zero.impl.db;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aion.crypto.HashUtil;
import org.aion.util.types.ByteArrayWrapper;
import org.aion.zero.impl.db.AionBlockStore.BlockInfo;
import org.junit.Test;

/** Test suite for {@link AionBlockStore.BlockInfoUnityV1} serialization */
public class BlockInfoUnityV1Test {

    private byte[] DEFAULT_HASH = HashUtil.h256("hello world".getBytes());

    @Test
    public void testBlockInfoSerialization() {
        BlockInfo info =
                new AionBlockStore.BlockInfoUnityV1(DEFAULT_HASH, DEFAULT_HASH, BigInteger.ONE, BigInteger.ONE, true);

        byte[] serialized =
                AionBlockStore.BLOCK_INFO_SERIALIZER.serialize(Collections.singletonList(info));
        System.out.println("serialized: " + ByteArrayWrapper.wrap(serialized));

        List<BlockInfo> deserializedBlockInfos =
                AionBlockStore.BLOCK_INFO_SERIALIZER.deserialize(serialized);
        assertThat(deserializedBlockInfos.size()).isEqualTo(1);

        BlockInfo deserializedInfo = deserializedBlockInfos.get(0);

        assertThat(deserializedInfo.getCummDifficulty()).isEqualTo(info.getCummDifficulty());
        assertThat(deserializedInfo.getHash()).isEqualTo(info.getHash());
        assertThat(deserializedInfo.isMainChain()).isEqualTo(info.isMainChain());
    }

    @Test
    public void testBlockInfoMultipleSerialization() {
        BlockInfo info =
                new AionBlockStore.BlockInfoUnityV1(DEFAULT_HASH, DEFAULT_HASH, BigInteger.ONE, BigInteger.ONE, true);
        BlockInfo info2 =
                new AionBlockStore.BlockInfoUnityV1(HashUtil.h256(DEFAULT_HASH), HashUtil.h256(DEFAULT_HASH), BigInteger.TWO, BigInteger.ONE, false);

        byte[] serialized =
                AionBlockStore.BLOCK_INFO_SERIALIZER.serialize(Arrays.asList(info, info2));
        System.out.println("serialized: " + ByteArrayWrapper.wrap(serialized));

        // deserialized
        List<BlockInfo> deserializedBlockInfos =
                AionBlockStore.BLOCK_INFO_SERIALIZER.deserialize(serialized);

        BlockInfo dInfo1 = deserializedBlockInfos.get(0);

        assertThat(dInfo1.getCummDifficulty()).isEqualTo(info.getCummDifficulty());
        assertThat(dInfo1.getHash()).isEqualTo(info.getHash());
        assertThat(dInfo1.isMainChain()).isEqualTo(info.isMainChain());

        BlockInfo dInfo2 = deserializedBlockInfos.get(1);

        assertThat(dInfo2.getCummDifficulty()).isEqualTo(info2.getCummDifficulty());
        assertThat(dInfo2.getHash()).isEqualTo(info2.getHash());
        assertThat(dInfo2.isMainChain()).isEqualTo(info2.isMainChain());
    }
}
