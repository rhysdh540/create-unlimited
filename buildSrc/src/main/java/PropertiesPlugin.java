import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PropertiesPlugin implements Plugin<Project> {

	private Project rootProject;

	@Override
	public void apply(@NotNull Project project) {
		rootProject = project.getRootProject();

		String mcVersion = "";
		List<String> mcVers = rootProject.fileTree("versionProperties").getFiles().stream()
			.map(file -> file.getName().replaceAll("\\.properties", ""))
			.sorted()
			.toList();

		println("Setting up properties...");
		println("Avalible Minecraft Versions: " + String.join(", ", mcVers));

		if(rootProject.hasProperty("mcVer")) {
			mcVersion = (String) rootProject.findProperty("mcVer");
		}

		if(!mcVers.contains(mcVersion)) {
			println("No mcVer set or the set mcVer is invalid!");
			println("Use -PmcVer='mc_version' or edit gradle.properties to set the minecraft version.");
			throw new RuntimeException("Invalid Minecraft Version");
		}

		println("Using Minecraft " + mcVersion);

		Properties properties = new Properties();
		try {
			properties.load(Files.newInputStream(rootProject.file("versionProperties/" + mcVersion + ".properties").toPath()));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to load version properties", e);
		}

		// extra properties
		String mcVersionRange = mcVers.stream().map(it -> "[" + it + "]").collect(Collectors.joining(","));
		setProperty("mcVersionRange", mcVersionRange);

		properties.forEach((key, value) -> setProperty(key.toString(), value));
	}

	private void println(String s) {
		rootProject.getLogger().lifecycle(s);
	}

	private void setProperty(String key, Object value) {
		rootProject.getExtensions().getExtraProperties().set(key, value);
	}
}
