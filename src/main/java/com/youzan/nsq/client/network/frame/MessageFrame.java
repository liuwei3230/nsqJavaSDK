package com.youzan.nsq.client.network.frame;

import java.util.Arrays;

public class MessageFrame extends NSQFrame {
    /*-
     * =========================================================================
     *                       NSQ the message format
     * =========================================================================
     */
    /**
     * 8-byte : nanosecond timestamp (int64)
     */
    private final byte[] timestamp = new byte[8];
    /**
     * 2-byte : (uint16)
     */
    private final byte[] attempts = new byte[2];
    /**
     * 16-byte : (hex string encoded in ASCII)
     */
    private final byte[] messageID = new byte[16];

    private final byte[] internalID = new byte[8];
    private final byte[] traceID = new byte[8];

    /**
     * N-byte : (binary)
     */
    private byte[] messageBody;
    /**
     * 8-byte : disk queue offset
     */
    private byte[] diskQueueOffset = null;
    /**
     * 4-byte : disk queue data size
     */
    private byte[] diskQueueDataSize = null;

    /*-
     * =========================================================================
     *                       NSQ the message format -- Ending
     * =========================================================================
     */

    /**
     * @return the timestamp
     */
    public byte[] getTimestamp() {
        return timestamp;
    }

    /**
     * @return the attempts
     */
    public byte[] getAttempts() {
        return attempts;
    }

    /**
     * @return the messageID
     */
    public byte[] getMessageID() {
        return messageID;
    }

    /**
     * @return the messageBody
     */
    public byte[] getMessageBody() {
        return messageBody;
    }

    public byte[] getTractID() {
        return this.traceID;
    }

    public byte[] getInternalID() {
        return this.internalID;
    }
    /**
     * @param messageBody the messageBody to set
     */
    private void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public void setData(byte[] bytes) {
        final int messageBodySize = bytes.length - (8 + 2 + 16);
        messageBody = new byte[messageBodySize];
        System.arraycopy(bytes, 0, timestamp, 0, 8);
        System.arraycopy(bytes, 8, attempts, 0, 2);
        System.arraycopy(bytes, 10, messageID, 0, 16);
        System.arraycopy(bytes, 10, internalID, 0, 8);
        System.arraycopy(bytes, 18, traceID, 0, 8);
        System.arraycopy(bytes, 26, messageBody, 0, messageBodySize);
    }

    @Override
    public FrameType getType() {
        return FrameType.MESSAGE_FRAME;
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "MessageFrame{" +
                " messageID=" + Arrays.toString(messageID) +
                ", attempts=" + Arrays.toString(attempts) +
                ", timestamp=" + Arrays.toString(timestamp) +
                '}';
    }

    /**
     * function to get diskQueueOffset of current msg, diskqueue has meaning only when message contains advanced info of
     * SUB
     * @return diskQueueOffSet (int 64) in byte[]
     */
    public byte[] getDiskQueueOffset(){
        if(null == this.diskQueueOffset) {
            this.diskQueueOffset = new byte[8];
            System.arraycopy(getMessageBody(), 0, this.diskQueueOffset, 0, 8);
        }
        return this.diskQueueOffset;
    }

    /**
     * function to get diskQueueDataSize of current msg, disk queue dat size has meaning only when message contains
     * advanced info of SUB
     * @return diskQueueDataSize (int 64) in byte[]
     */
    public byte[] getDiskQueueDataSize(){
        if(null == this.diskQueueDataSize){
            this.diskQueueDataSize = new byte[4];
            System.arraycopy(getMessageBody(), 8, this.diskQueueDataSize, 0, 4);
        }
        return this.diskQueueDataSize;
    }
}
