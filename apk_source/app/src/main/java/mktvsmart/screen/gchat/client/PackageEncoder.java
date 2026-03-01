package mktvsmart.screen.gchat.client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/* loaded from: classes.dex */
public class PackageEncoder implements MessageEncoder<TransmissionPackage> {
    @Override // org.apache.mina.filter.codec.demux.MessageEncoder
    public void encode(IoSession session, TransmissionPackage message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(message.getLength()).setAutoExpand(true);
        buffer.putInt(message.getLength());
        buffer.putShort(message.getHeader());
        buffer.putShort(message.getType());
        if (message.getBody() != null) {
            buffer.put(message.getBody());
        }
        buffer.flip();
        out.write(buffer);
    }
}
