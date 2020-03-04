package virtuoel.statement.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

public class StatementMixinConfigPlugin implements IMixinConfigPlugin
{
	private static final String MIXIN_PACKAGE = "virtuoel.statement.mixin";
	
	@Override
	public void onLoad(String mixinPackage)
	{
		if (!mixinPackage.startsWith(MIXIN_PACKAGE))
		{
			throw new IllegalArgumentException(
				String.format("Invalid package: Expected \"%s\", but found \"%s\".", MIXIN_PACKAGE, mixinPackage)
			);
		}
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if (!mixinClassName.startsWith(MIXIN_PACKAGE))
		{
			throw new IllegalArgumentException(
				String.format("Invalid package for class \"%s\": Expected \"%s\", but found \"%s\".", targetClassName, MIXIN_PACKAGE, mixinClassName)
			);
		}
		
		if (
			(mixinClassName.contains(".compat114.") && MINOR != 14) ||
			(mixinClassName.contains(".compat114plus.") && MINOR < 14) ||
			(mixinClassName.contains(".compat115.") && MINOR != 15) ||
			(mixinClassName.contains(".compat115plus.") && MINOR < 15) ||
			(mixinClassName.contains(".compat116.") && MINOR != 16) ||
			(mixinClassName.contains(".compat116plus.") && MINOR < 16) ||
			(mixinClassName.contains(".compat117.") && MINOR != 17) ||
			(mixinClassName.contains(".compat117plus.") && MINOR < 17)
		)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
		
	}
	
	@Override
	public List<String> getMixins()
	{
		return null;
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
	
	private static final SemanticVersion MINECRAFT_VERSION = lookupMinecraftVersion();
	protected static final int MAJOR = getVersionComponent(0);
	protected static final int MINOR = getVersionComponent(1);
	protected static final int PATCH = getVersionComponent(2);
	
	private static SemanticVersion lookupMinecraftVersion()
	{
		final Version version = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion();
		
		return (SemanticVersion) (version instanceof SemanticVersion ? version : null);
	}
	
	private static int getVersionComponent(int pos)
	{
		return MINECRAFT_VERSION != null ? MINECRAFT_VERSION.getVersionComponent(pos) : -1;
	}
}
