package luaforge.core;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import java.io.File;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.util.ArrayList;
import luaforge.core.api.LuaClassRegistry;

import luaforge.core.lua.LuaEnvironment;
import luaforge.core.lua.LuaStartup;
import luaforge.core.lua.libs.*;
import luaforge.core.lua.libs.block.BlockLib;
import luaforge.core.lua.libs.block.BlockTemplate;
import luaforge.core.lua.libs.item.ItemLib;
import luaforge.core.lua.libs.item.ItemTemplate;
import luaforge.core.proxies.CommonProxy;
import net.minecraft.src.ItemStack;

@Mod(modid = "LuaForge", name = "LuaForge", version = "1.0.0", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class Core {

    public static final String dirName = "luaforge-mods";
    public static ArrayList<LuaEnvironment> LuaMods = new ArrayList<LuaEnvironment>();
    
    @Instance("LuaForge")
    public static Core core;
    
    @SidedProxy(clientSide = "luaforge.core.proxies.ClientProxy", serverSide = "luaforge.core.proxies.CommonProxy")
    public static CommonProxy proxy;

    @PreInit
    public void preLoad(FMLPreInitializationEvent event) {
        registerDefaultLibs();
        File folder = new File(Minecraft.getMinecraftDir(), dirName);

        if (folder.exists() && folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            if(listOfFiles.length == 1){
                Log.info(listOfFiles.length + " mod found in " + dirName + " now loading");
            }else{
                Log.info(listOfFiles.length + " mods found in " + dirName + " now loading");
            }
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isDirectory()) {
                    LuaEnvironment env = new LuaEnvironment(listOfFiles[i], listOfFiles[i].getName());
                    LuaMods.add(env);
                }
            }
        } else {
            if (folder.exists() && folder.isDirectory()) {
                Log.info(dirName + " found");
            } else {
                if (folder.mkdirs()) {
                    Log.info(dirName + " created");
                } else {
                    Log.severe("Error creating " + dirName);
                }
            }

        }
        loadLuaMod(LuaStartup.PRESTARTUP);

    }

    @Init
    public void load(FMLInitializationEvent event) {
        loadLuaMod(LuaStartup.STARTUP);
        proxy.registerRenderers();
        for (BlockTemplate bt : BlockLib.regularBlocks.values()) {
            GameRegistry.registerBlock(bt);
            LanguageRegistry.addName(bt, bt.getVisibleName());
        }
        for (ItemTemplate it : ItemLib.regularItems.values()) {
            LanguageRegistry.addName(it, it.getVisibleName());
        }
        Log.info("Sucessfully loaded");
    }

    @PostInit
    public void postLoad(FMLPostInitializationEvent event) {
        loadLuaMod(LuaStartup.POSTSTARTUP);
    }

    @ServerStarting
    public void serverStarting(FMLServerStartingEvent event) {
        loadLuaMod(LuaStartup.SERVERSTARTUP);
    }

    public void loadLuaMod(LuaEnvironment env, LuaStartup startup) {
        if (env.startup == startup) {
            env.call();
        }
    }

    public void loadLuaMod(LuaStartup startup) {
        for (LuaEnvironment e : LuaMods) {
            loadLuaMod(e, startup);
        }
    }

    private static void registerDefaultLibs() {
        LuaClassRegistry.register(new LogLib());
        LuaClassRegistry.register(new ClientLib());
        LuaClassRegistry.register(new BlockLib());
        LuaClassRegistry.register(new ItemLib());
        LuaClassRegistry.register(new ReferenceLib());
        LuaClassRegistry.register(new CraftingHandler());
    }
    
    
    
}