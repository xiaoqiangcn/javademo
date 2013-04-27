package com.johhny.java.demo.btrace;

import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

@BTrace
public class BTraceScript1 {
   @OnMethod(
      clazz="com.johhny.java.demo.btrace.Counter",
      method="add"
   )
   public static void traceExecute(int num){
	   println("====== "); 
	   println(strcat("parameter num: ",str(num)));  
   }
}
