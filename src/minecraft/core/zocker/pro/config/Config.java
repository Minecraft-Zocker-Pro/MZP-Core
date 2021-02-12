package minecraft.core.zocker.pro.config;

import com.vdurmont.semver4j.Semver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

	private static final List<Config> CONFIGS = new ArrayList<>();

	private String pluginName;
	private String name;
	private String path;
	private String version;
	private boolean firstSetup;
	private YamlConfiguration configuration;

	public Config(String fileName, String pluginName) {
		for (Config config : CONFIGS) {
			if (config.getPluginName().equalsIgnoreCase(pluginName) && config.getName().equalsIgnoreCase(fileName)) return;
		}

		this.name = fileName;
		this.pluginName = pluginName;
		this.path = "plugins/" + pluginName + "/";


		try {
			File folder = new File("plugins/" + pluginName + "/");
			if (!folder.exists()) {
				boolean result = folder.mkdirs();
				if (!result) return; // TODO error message
			}

			File file = new File(this.path + this.name);
			if (!file.exists()) {
				boolean result = file.createNewFile();
				if (!result) return; // TODO error message
				this.configuration = YamlConfiguration.loadConfiguration(file);
				this.configuration.set("config.version", "0.0.1");
				this.configuration.set("config.setup", true);
				this.version = "0.0.1";
				this.firstSetup = true;
			} else {
				this.configuration = YamlConfiguration.loadConfiguration(file);
				this.version = this.configuration.getString("config.version");
				this.firstSetup = false;
			}

			this.configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		CONFIGS.add(this);
	}

	public void set(String keyPath, Object value) {
		this.configuration.set(keyPath, value);
	}

	public void set(String keyPath, Object value, String version) { // in build 0.0.2
		if (this.isFirstSetup()) {
			this.configuration.set(keyPath, value);
			this.setVersion(version, true);
			return;
		}

		Semver semver = new Semver(version);
		if (semver.isGreaterThan(Objects.requireNonNull(this.configuration.getString("config.version")))) { // config version
			this.configuration.set(keyPath, value);
			this.setVersion(version, false);
		}
	}

	public ConfigurationSection getSection(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getConfigurationSection(keyPath);
		}
		return null;
	}

	public String getSectionString(String section, String keyPath) {
		if (keyPath.length() > 0) {
			String value = this.getSection(section).getString(keyPath);
			if (value != null) {
				return value.replaceAll("&", "§");
			}
		}
		return null;
	}

	public List<String> getSectionStringList(String section, String keyPath) {
		if (keyPath.length() > 0) {
			List<String> values = this.getSection(section).getStringList(keyPath);
			List<String> newValues = new ArrayList<>();
			if (!values.isEmpty()) {
				for (String string : values) {
					newValues.add(string.replaceAll("&", "§"));
				}
			}
			return newValues;
		}
		return null;
	}

	/**
	 * Read the key from the file
	 *
	 * @param keyPath The path to read the key
	 * @return value
	 */
	public String getString(String keyPath) {
		if (keyPath == null) throw new NullPointerException("Config keypath is null.");

		String value = this.configuration.getString(keyPath);
		if (value == null) return null;
		value = value.replaceAll("&", "§");
		return value;
	}

	/**
	 * Read the key from the file and replace the & symbol to § for showing colours
	 *
	 * @param keyPath     The path to read the key
	 * @param formatColor Convert & to § for showing colours
	 * @return String
	 */
	public String getString(String keyPath, boolean formatColor) {
		if (keyPath == null) throw new NullPointerException("Config keypath is null.");

		String value = this.configuration.getString(keyPath);
		if (value == null) return null;
		if (!formatColor) {
			return value;
		}

		value = value.replaceAll("&", "§");
		return value;
	}

	public List<String> getStringList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getStringList(keyPath);
		}
		return null;
	}

	public List<String> getStringList(String keyPath, boolean formatColor) {
		if (keyPath.length() > 0) {
			List<String> stringList = this.configuration.getStringList(keyPath);

			if(!formatColor){
				return stringList;
			}

			return stringList.stream().map( string ->
					ChatColor.translateAlternateColorCodes('&', string)
			).collect(Collectors.toList());
		}
		return null;
	}

	public List<Boolean> getBooleanList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getBooleanList(keyPath);
		}
		return null;
	}

	public List<Integer> getIntList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getIntegerList(keyPath);
		}
		return null;
	}

	public List<Double> getDoubleList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getDoubleList(keyPath);
		}
		return null;
	}

	public List<Float> getFloatList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getFloatList(keyPath);
		}
		return null;
	}

	public List<Long> getLongList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getLongList(keyPath);
		}
		return null;
	}

	public List<?> getList(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getList(keyPath);
		}
		return null;
	}

	public int getInt(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getInt(keyPath);
		}
		return 0;
	}

	public double getDouble(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getDouble(keyPath);
		}
		return 0;
	}

	public boolean getBool(String keyPath) {
		if (keyPath.length() > 0) {
			return this.configuration.getBoolean(keyPath);
		}

		return false;
	}

	public long getLong(String keyPath) {
		return this.configuration.getLong(keyPath);
	}

	public void save() {
		try {
			this.configuration.save(new File(this.path + this.name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(new File(path + name));
	}

	public String getPluginName() {
		return this.pluginName;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public String getVersion() {
		return version;
	}

	public boolean isFirstSetup() {
		return firstSetup;
	}

	public void setFirstSetup(boolean firstSetup) {
		this.firstSetup = firstSetup;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setVersion(String version, boolean saveFile) {
		this.version = version;
		if (!saveFile) return;

		try {
			this.configuration.set("config.version", version);
			this.configuration.save(new File(this.path + this.name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Config getConfig(String fileName, String pluginName) {
		for (Config config : CONFIGS) {
			if (config.getPluginName().equalsIgnoreCase(pluginName) && config.getName().equalsIgnoreCase(fileName)) return config;
		}
		return null;
	}
}
