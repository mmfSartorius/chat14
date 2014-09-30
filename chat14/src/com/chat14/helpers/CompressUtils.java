package com.chat14.helpers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import com.chat14.helpers.model.CompressedData;

public class CompressUtils {
    private static LZ4Factory factory = LZ4Factory.fastestInstance();
    public static final Charset DEFAULT = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Integer MAX_MESSAGE_SIZE = 3000;
    private static final Logger LOGGER = Logger.getLogger(CompressUtils.class);

    public static List<CompressedData> getCompressedAndChunkedData(String json, int commandType) {
        boolean isCompressed;
        byte[] jsonData = json.getBytes(UTF_8);
        final int decompressedLength = jsonData.length;
        // compress data
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(jsonData, 0, decompressedLength, compressed, 0, maxCompressedLength);
        int messageNumber;
        int finalLength;
        byte[] finalData;
        if (compressedLength >= decompressedLength) {
            LOGGER.info(String.format("Skipping compression initial size: %d, compressed size: %d", decompressedLength, compressedLength));
            isCompressed = false;
            finalLength = decompressedLength;
            finalData = jsonData;
        } else {
            isCompressed = true;
            finalLength = compressedLength;
            finalData = compressed;
        }
        messageNumber = (finalLength + MAX_MESSAGE_SIZE - 1) / MAX_MESSAGE_SIZE;
        List<CompressedData> chunked = new ArrayList<CompressedData>(messageNumber);
        String UUID = Generator.getInstance().getRandomUUID();
        for (int i = 0; i < messageNumber; i++) {
            CompressedData data = new CompressedData();
            data.setCommandType(commandType);
            data.setCompressed(isCompressed);
            data.setCompressedPayload(new String(finalData, MAX_MESSAGE_SIZE * i, (i != messageNumber - 1) ? MAX_MESSAGE_SIZE : finalLength % MAX_MESSAGE_SIZE,
                    DEFAULT));
            if (messageNumber > 1) {
                data.setMessageId(UUID);
                data.setSequenceNumber(i);
                data.setTotalNumber(messageNumber);
            }
            if (isCompressed) {
                data.setDecompressedSize(decompressedLength);
            }
            chunked.add(data);
        }
        return chunked;
    }

    public static String getDecompressedMessage(CompressedData data) {
        if (Boolean.TRUE.equals(data.getCompressed())) {
            return getDecompressedMessage(data.getCompressedPayload(), data.getDecompressedSize());
        }
        return data.getCompressedPayload();
    }

    public static String getDecompressedMessage(String compressed, int decompressedLength) {
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[decompressedLength];
        decompressor.decompress(compressed.getBytes(DEFAULT), 0, restored, 0, decompressedLength);
        return new String(restored, UTF_8);
    }

    public static String getDecompressedMessage(List<CompressedData> src) {
        Collections.sort(src);
        StringBuilder compressed = new StringBuilder();
        for (CompressedData data : src) {
            compressed.append(data.getCompressedPayload());
        }
        if (!src.isEmpty() && src.get(0).getCompressed()) {
            return getDecompressedMessage(compressed.toString(), src.get(0).getDecompressedSize());
        } else {
            return compressed.toString();
        }
    }
}
