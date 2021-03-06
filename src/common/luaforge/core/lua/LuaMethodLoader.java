package luaforge.core.lua;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import luaforge.core.Log;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public abstract class LuaMethodLoader extends OneArgFunction {

    public Globals globals;
    public LuaEnvironment luaEnv;
    protected Method[] methods;

    public LuaMethodLoader(LuaEnvironment luaEnv) {
        this.luaEnv = luaEnv;
    }
    
    final class LuaFunctionLoader extends VarArgFunction {

        public LuaFunctionLoader(int opcode, String name) {
            this.opcode = opcode;
            this.name = name;
        }

        @Override
        public Varargs invoke(Varargs args) {
            for (int i = 0; i < methods.length; i++) {
                if (i == opcode) {
                    try {
                        int len = methods[i].getParameterTypes().length;
                        if(len == 1){
                            return (Varargs) methods[i].invoke(null, args);
                        } else if (len == 2) {
                            return (Varargs) methods[i].invoke(null, args, luaEnv);
                        } else {
                            throw new Exception("Invalid length of parameters in method " + methods[i].getName());
                        }
                        
                    } catch (Exception e) {
                        if(e.getCause() instanceof LuaError) {
                            return error(e.getCause().getMessage());
                        }
                        Log.severe("Invocation exception");
                        if(e.getMessage() == null) {
                            Log.severe(e.getCause().getMessage());
                            e.getCause().printStackTrace();
                        } else {
                            Log.severe(e.getMessage());
                            e.printStackTrace();
                        }
                        
                    }
                    break;
                }
            }
            return NONE;
        }
    }
}
