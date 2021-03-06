package luaforge.core.lua;

import java.lang.reflect.Method;

import luaforge.core.Log;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaIndexMethodLoader extends LuaMethodLoader {

    private String indexName;

    public LuaIndexMethodLoader(LuaEnvironment luaEnv, String name, Method... methods) {
        super(luaEnv);
        indexName = name;
        this.methods = methods;
    }

    @Override
    public LuaValue call(LuaValue env) {
        globals = env.checkglobals();
        LuaTable t = new LuaTable();

        for (int i = 0; i < methods.length; i++) {
            t.set(methods[i].getName(), new LuaFunctionLoader(i, methods[i].getName()));
        }

        env.set(indexName, t);
        return t;
    }
}
