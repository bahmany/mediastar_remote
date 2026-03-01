package mktvsmart.screen.gchat.client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/* loaded from: classes.dex */
public class PackageDecoder extends MessageDecoderAdapter {
    @Override // org.apache.mina.filter.codec.demux.MessageDecoder
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        int packageLength = in.remaining();
        int length = in.getInt();
        if (packageLength >= length) {
            short header = in.getShort();
            if (header != 0) {
                return MessageDecoderResult.NOT_OK;
            }
            return MessageDecoderResult.OK;
        }
        return MessageDecoderResult.NEED_DATA;
    }

    @Override // org.apache.mina.filter.codec.demux.MessageDecoder
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int packageLength = in.remaining();
        int length = in.getInt();
        if (packageLength >= length) {
            short header = in.getShort();
            if (header != 0) {
                return MessageDecoderResult.NOT_OK;
            }
            short type = in.getShort();
            TransmissionPackage transmissionPackage = new TransmissionPackage();
            transmissionPackage.setLength(length);
            transmissionPackage.setHeader(header);
            transmissionPackage.setType(type);
            if (length - 8 > 0) {
                byte[] body = new byte[length - 8];
                in.get(body);
                transmissionPackage.setBody(body);
            }
            out.write(transmissionPackage);
            return MessageDecoderResult.OK;
        }
        return MessageDecoderResult.NEED_DATA;
    }
}
