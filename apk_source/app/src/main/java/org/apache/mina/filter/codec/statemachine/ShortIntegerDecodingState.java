package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/* loaded from: classes.dex */
public abstract class ShortIntegerDecodingState implements DecodingState {
    private int counter;
    private int highByte;

    protected abstract DecodingState finishDecode(short s, ProtocolDecoderOutput protocolDecoderOutput) throws Exception;

    @Override // org.apache.mina.filter.codec.statemachine.DecodingState
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        while (in.hasRemaining()) {
            switch (this.counter) {
                case 0:
                    this.highByte = in.getUnsigned();
                    this.counter++;
                case 1:
                    this.counter = 0;
                    return finishDecode((short) ((this.highByte << 8) | in.getUnsigned()), out);
                default:
                    throw new InternalError();
            }
        }
        return this;
    }

    @Override // org.apache.mina.filter.codec.statemachine.DecodingState
    public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
        throw new ProtocolDecoderException("Unexpected end of session while waiting for a short integer.");
    }
}
