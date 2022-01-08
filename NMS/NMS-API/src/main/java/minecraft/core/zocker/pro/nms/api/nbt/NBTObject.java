package minecraft.core.zocker.pro.nms.api.nbt;

import java.util.Set;

public interface NBTObject {

	String asString();

	boolean asBoolean();

	int asInt();

	double asDouble();

	long asLong();

	short asShort();

	byte asByte();

	int[] asIntArray();

	byte[] asByteArray();

	Set<String> getKeys();

	NBTCompound asCompound();
}
