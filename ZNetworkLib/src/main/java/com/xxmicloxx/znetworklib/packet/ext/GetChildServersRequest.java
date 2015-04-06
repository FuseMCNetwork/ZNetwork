package com.xxmicloxx.znetworklib.packet.ext;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;
import com.xxmicloxx.znetworklib.codec.Request;

/**
 * Created by ml on 04.07.14.
 */
public class GetChildServersRequest implements Request {
    private String nameMatcher;
    private String typeMatcher;

    public String getNameMatcher() {
        return nameMatcher;
    }

    public void setNameMatcher(String nameMatcher) {
        this.nameMatcher = nameMatcher;
    }

    public String getTypeMatcher() {
        return typeMatcher;
    }

    public void setTypeMatcher(String typeMatcher) {
        this.typeMatcher = typeMatcher;
    }

    @Override
    public com.xxmicloxx.znetworklib.codec.CodecResult write(PacketWriter writer) {
        writer.writeString(nameMatcher);
        writer.writeString(typeMatcher);
        return CodecResult.OK;
    }

    @Override
    public com.xxmicloxx.znetworklib.codec.CodecResult read(PacketReader reader) {
        nameMatcher = reader.readString();
        typeMatcher = reader.readString();
        return CodecResult.OK;
    }
}
