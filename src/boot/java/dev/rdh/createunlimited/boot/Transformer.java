package dev.rdh.createunlimited.boot;

import io.github.prcraftmc.classdiff.ClassPatcher;
import io.github.prcraftmc.classdiff.format.DiffReader;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.ReEntranceLock;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ALL")
public abstract class Transformer implements IMixinService, IClassBytecodeProvider, IExtension, IMixinConfigPlugin {
	private static final ILogger LOGGER = MixinService.getService().getLogger("CreateUnlimited/Boot");
	private final ClassLoader CL = getClass().getClassLoader();

	protected Transformer() {
		LOGGER.info("Loading Transformer...");
		LOGGER.info("Class: {}, loader: {}", getClass().getName(), CL);
		LOGGER.info("Platform: {}", platform);
	}

	// region IMixinService
	private IMixinService serviceDelegate;

	@Override
	public String getName() {
		return serviceDelegate.getName();
	}

	@Override
	public boolean isValid() {
		return serviceDelegate.isValid();
	}

	@Override
	public void prepare() {
		serviceDelegate.prepare();
	}

	@Override
	public Phase getInitialPhase() {
		return serviceDelegate.getInitialPhase();
	}

	@Override
	public void offer(IMixinInternal internal) {
		serviceDelegate.offer(internal);
	}

	@Override
	public void init() {
		serviceDelegate.init();
	}

	@Override
	public void beginPhase() {
		serviceDelegate.beginPhase();
	}

	@Override
	public void checkEnv(Object bootSource) {
		serviceDelegate.checkEnv(bootSource);
	}

	@Override
	public ReEntranceLock getReEntranceLock() {
		return serviceDelegate.getReEntranceLock();
	}

	@Override
	public IClassProvider getClassProvider() {
		return serviceDelegate.getClassProvider();
	}

	@Override
	public IClassBytecodeProvider getBytecodeProvider() {
		bytecodeDelegate = serviceDelegate.getBytecodeProvider();
		return this;
	}

	@Override
	public ITransformerProvider getTransformerProvider() {
		return serviceDelegate.getTransformerProvider();
	}

	@Override
	public IClassTracker getClassTracker() {
		return serviceDelegate.getClassTracker();
	}

	@Override
	public IMixinAuditTrail getAuditTrail() {
		return serviceDelegate.getAuditTrail();
	}

	@Override
	public Collection<String> getPlatformAgents() {
		return serviceDelegate.getPlatformAgents();
	}

	@Override
	public IContainerHandle getPrimaryContainer() {
		return serviceDelegate.getPrimaryContainer();
	}

	@Override
	public Collection<IContainerHandle> getMixinContainers() {
		return serviceDelegate.getMixinContainers();
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return serviceDelegate.getResourceAsStream(name);
	}

	@Override
	public String getSideName() {
		return serviceDelegate.getSideName();
	}

	@Override
	public CompatibilityLevel getMinCompatibilityLevel() {
		return serviceDelegate.getMinCompatibilityLevel();
	}

	@Override
	public CompatibilityLevel getMaxCompatibilityLevel() {
		return serviceDelegate.getMaxCompatibilityLevel();
	}

	@Override
	public ILogger getLogger(String name) {
		return serviceDelegate.getLogger(name);
	}
	// endregion

	// region IClassBytecodeProvider
	private IClassBytecodeProvider bytecodeDelegate;
	private final Map<String, ClassNode> classes = new HashMap<>();

	public boolean addClass(ClassNode classNode) {
		return classes.putIfAbsent(classNode.name.replace('/', '.'), classNode) == null;
	}

	@Override
	public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
		ClassNode classNode = classes.get(name);
		if (classNode != null) {
			return classNode;
		}

		return bytecodeDelegate.getClassNode(name);
	}

	@Override
	public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
		ClassNode classNode = classes.get(name);
		if (classNode != null) {
			return classNode;
		}

		return bytecodeDelegate.getClassNode(name, runTransformers);
	}

	@Override
	public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags) throws ClassNotFoundException, IOException {
		ClassNode classNode = classes.get(name);
		if (classNode != null) {
			return classNode;
		}

		return bytecodeDelegate.getClassNode(name, runTransformers, readerFlags);
	}

	// endregion

	// region IExtension
	private boolean bootstrapped = false;
	private void extensionBootstrap() {
		if (bootstrapped) return;
		bootstrapped = true;

		LOGGER.info("Bootstrapping Transformer as IExtension...");
		IMixinTransformer transformer = (IMixinTransformer) MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
		Extensions extensions = (Extensions) transformer.getExtensions();
		extensions.add(this);
	}

	@Override
	public boolean checkActive(MixinEnvironment environment) {
		return true;
	}

	@Override
	public void preApply(ITargetClassContext context) {
		transform(context.getClassNode());
	}

	@Override
	public void postApply(ITargetClassContext context) {

	}

	@Override
	public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

	}
	// endregion

	// region IMixinConfigPlugin
	private String mixinPackage;

	@Override
	public void onLoad(String mixinPackage) {
		this.mixinPackage = mixinPackage;
		extensionBootstrap();
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return mixinClassName.endsWith("$Patched") || !patches.containsKey(mixinClassName);
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		Class<?> myTargetsClass = myTargets.getClass();

		Map<String, List<?>> backingTargets = null;
		for (Field field : myTargetsClass.getDeclaredFields()) {
			if (Map.class.isAssignableFrom(field.getType()) && field.isSynthetic()) {
				backingTargets = getField(field, myTargets);
				break;
			}
		}

		if (backingTargets == null) {
			StringBuilder sb = new StringBuilder("Failed to find backing map of Set class ")
				.append(myTargetsClass.getName()).append('\n').append("Fields:").append('\n');
			for (Field field : myTargetsClass.getDeclaredFields()) {
				sb.append('\t').append(field.getName()).append(": ").append(field.getType().getName());
				if (field.isSynthetic()) {
					sb.append(" (synthetic)");
				}
				sb.append('\n');
			}

			throw new NoSuchFieldException(sb.toString());
		}

		for (String className : getTransformedClasses()) {
			// allocate a new list just in case mixins target this class
			backingTargets.putIfAbsent(className, new ArrayList<>());
		}
	}

	private void injectService() {
		MixinService service = (MixinService) L.unreflect(MixinService.class.getDeclaredMethod("getInstance")).invokeExact();

		// set the global service
		Field f = MixinService.class.getDeclaredField("service");
		IMixinService s = getField(f, service);
		if (s == this) return; // already set?
		LOGGER.info("Bootstrapping Transformer as IMixinService...");
		this.serviceDelegate = s;

		setField(f, service, this);

		Object transformer = MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
		Object processor = getField(transformer.getClass().getDeclaredField("processor"), transformer);

		// set the service on the processor
		setField(processor.getClass().getDeclaredField("service"), processor, this);

		List<IMixinConfig> configs = getField(processor.getClass().getDeclaredField("pendingConfigs"), processor);

		for (IMixinConfig config : configs) {
			if (config.getPlugin() == this) {
				// set it on this config too, since we kinda need that
				setField(config.getClass().getDeclaredField("service"), config, this);
				break;
			}
		}
	}

	@Override
	public List<String> getMixins() {
		injectService();
		return getTransformedMixins(this.mixinPackage);
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
	// endregion

	// region Reflection
	private static final Unsafe U = getUnsafe();
	private static final MethodHandles.Lookup L = getImplLookup();

	private static Unsafe getUnsafe() {
		for(Field field : Unsafe.class.getDeclaredFields()) {
			if(field.getType() == Unsafe.class) {
				field.setAccessible(true);
				return (Unsafe) field.get(null);
			}
		}
		throw new RuntimeException("Unsafe not found");
	}

	private static MethodHandles.Lookup getImplLookup() {
		return getField(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"), null);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getField(final Field field, final Object instance) {
		Object base;
		long offset;
		if (Modifier.isStatic(field.getModifiers())) {
			if (instance != null) {
				throw new IllegalArgumentException("Instance must be null for static fields");
			}
			base = U.staticFieldBase(field);
			offset = U.staticFieldOffset(field);
		} else {
			if (instance == null) {
				throw new IllegalArgumentException("Instance must not be null for non-static fields");
			}
			base = instance;
			offset = U.objectFieldOffset(field);
		}

		return (T) U.getObject(base, offset);
	}

	private static <T> void setField(final Field field, final Object instance, final T value) {
		if (!field.getType().isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Value type " + value.getClass() + " does not match field type " + field.getType());
		}

		Object base;
		long offset;
		if (Modifier.isStatic(field.getModifiers())) {
			if (instance != null) {
				throw new IllegalArgumentException("Instance must be null for static fields");
			}
			base = U.staticFieldBase(field);
			offset = U.staticFieldOffset(field);
		} else {
			if (instance == null) {
				throw new IllegalArgumentException("Instance must not be null for non-static fields");
			}
			base = instance;
			offset = U.objectFieldOffset(field);
		}

		U.putObject(base, offset, value);
	}

	// region what
	private boolean isClassLoaded(String className) {
		MethodHandle mh = L.unreflect(
			ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class)
		).bindTo(CL);
		return (Class<?>) mh.invokeExact(className) != null;
	}

	protected void loadClass(ClassNode classNode) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(cw);
		byte[] classBytes = cw.toByteArray();

		MethodHandle mh = L.unreflect(
			ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class)
		).bindTo(CL);
		Class<?> c = (Class<?>) mh.invokeExact(classNode.name.replace('/', '.'), classBytes, 0, classBytes.length);
	}

	protected void earlyBoot(String name) {
		ClassNode n = MixinService.getService().getBytecodeProvider().getClassNode(name.replace(".", "/"));
		transform(n);
		loadClass(n);
	}
	// endregion
	// endregion

	// region Platform
	public final String platform = getPlatform();

	protected abstract String getPlatform();
	// endregion

	// region Patch Loader

	/**
	 * key is binary name (with dots)
	 */
	private final Map<String, byte[]> patches = getPatches();

	private Map<String, byte[]> getPatches() {
		Map<String, byte[]> patches = new HashMap<>();

		if (!CL.getResources("META-INF/patches.zip").hasMoreElements()) {
			LOGGER.info("No patches found");
			return patches;
		}

		ZipInputStream in = new ZipInputStream(CL.getResourceAsStream("META-INF/patches.zip"));
		for(ZipEntry entry; (entry = in.getNextEntry()) != null;) {
			String[] split = entry.getName().split("/", 2);
			String platformName = split[0];
			if (!platformName.equals(platform)) {
				continue;
			}

			if (!split[1].endsWith(".class.diff")) {
				throw new IllegalStateException("Unsupported file name: " + platformName);
			}

			String className = split[1].substring(0, split[1].length() - ".class.diff".length())
				.replace('/', '.');

			byte[] bytes = in.readAllBytes();

			patches.put(className, bytes);
		}

		LOGGER.info("Loaded {} patches for platform {}", patches.size(), platform);
		return patches;
	}
	// endregion

	// region Transformer

	/**
	 * @implNote this method (and the next) both assume that patches won't add or remove the @Mixin annotation
	 * from any classes (which is probably a safe bet) (but don't do that cause it'll break otherwise)
	 */
	protected Set<String> getTransformedClasses() {
		Set<String> transformedClasses = new HashSet<>();
		for(String className : patches.keySet()) {
			if (isClassLoaded(className)) {
				// mixin will otherwise throw a very unhelpful "re-entrance error"
				LOGGER.warn("Class {} is already loaded, skipping patch", className);
				continue;
			}

			byte[] classBytes = CL.getResourceAsStream(className.replace('.', '/') + ".class")
				.readAllBytes();
			ClassNode classNode = new ClassNode();
			new ClassReader(classBytes).accept(classNode, ClassReader.SKIP_DEBUG);

			if(!hasMixin(classNode)) {
				transformedClasses.add(className);
			}
		}

		return transformedClasses;
	}

	// TODO: does not work with accessor mixins
	private List<String> getTransformedMixins(String packag) {
		List<String> mixins = new ArrayList<>();

		for(String className : patches.keySet()) {
			byte[] classBytes = CL.getResourceAsStream(className.replace('.', '/') + ".class")
				.readAllBytes();
			ClassNode classNode = new ClassNode();
			new ClassReader(classBytes).accept(classNode, 0);

			if(!hasMixin(classNode)) continue;

			if (!className.startsWith(packag)) {
				LOGGER.warn("found mixin {} outside package {}", className, packag);
				continue;
			}

			transform(classNode);
			SimpleRemapper remapper = new SimpleRemapper(classNode.name, classNode.name + "$Patched");
			ClassNode remapped = new ClassNode();
			classNode.accept(new ClassRemapper(remapped, remapper));
			classNode = remapped;

			if (!addClass(classNode)) {
				LOGGER.warn("Failed to add mixin class {} to Transformer", classNode.name);
			}

			mixins.add(classNode.name
				.replace('/', '.')
				.substring(packag.length() + 1) // +1 to remove the dot
			);
		}

		LOGGER.info("Loaded {} mixins for package {}", mixins.size(), packag);
		return mixins;
	}

	private static boolean hasMixin(ClassNode classNode) {
		if (classNode.invisibleAnnotations == null) {
			return false;
		}

		for (AnnotationNode annotationNode : classNode.invisibleAnnotations) {
			if (annotationNode.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
				return true;
			}
		}

		return false;
	}

	public void transform(ClassNode node) {
		byte[] patch = patches.get(node.name.replace('/', '.'));
		if (patch == null) return;

		LOGGER.info("patching {}", node.name);

		try {
			ClassPatcher.patch(node, new DiffReader(patch));
		} catch (Exception e) {
			LOGGER.error("Failed to patch class " + node.name, e);
			// dump class and patch to files for easier debugging
			Path p = Path.of("patch-error-" + node.name.replace('/', '_') + ".class");
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(cw);
			Files.write(p, cw.toByteArray());
			Files.write(Path.of(p.toString() + ".diff"), patch);
		}
	}
	// endregion
}
