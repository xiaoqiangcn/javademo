package com.johhny.java.demo.btrace;


import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

@BTrace
public class BTraceScript2 {
   @OnMethod(
      clazz="com.johhny.java.demo.btrace.Counter",
      method="add",
      location=@Location(Kind.RETURN)
   )
   public static void traceExecute(@Self com.johhny.java.demo.btrace.Counter c,int num,@Return long result){
	   println("====== "); 
	   println(strcat("parameter num: ",str(num)));  
	   println(strcat("total num: ", str(get(field("com.johhny.java.demo.btrace.Counter","totalNum"), c))));  
	   println(strcat("return value:",str(result)));  
	   jstack();

   }
}
