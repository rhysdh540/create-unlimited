package dev.rdh.createunlimited.util;

public class VersionUtils {

	public static String getVersion() {
		#if MC_1_19_2
		return "1.19.2";
		#elif MC_1_20_1
		return "1.20.1";
		#else
		return "unknown"
		#endif
	}
}
