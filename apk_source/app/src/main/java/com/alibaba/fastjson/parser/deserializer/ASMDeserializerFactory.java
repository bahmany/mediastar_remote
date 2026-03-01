package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.asm.ASMException;
import com.alibaba.fastjson.asm.ClassWriter;
import com.alibaba.fastjson.asm.FieldVisitor;
import com.alibaba.fastjson.asm.Label;
import com.alibaba.fastjson.asm.MethodVisitor;
import com.alibaba.fastjson.asm.Opcodes;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.ASMClassLoader;
import com.alibaba.fastjson.util.ASMUtils;
import com.alibaba.fastjson.util.DeserializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public class ASMDeserializerFactory implements Opcodes {
    private static final ASMDeserializerFactory instance = new ASMDeserializerFactory();
    private final ASMClassLoader classLoader;
    private final AtomicLong seed;

    public String getGenClassName(Class<?> clazz) {
        return "Fastjson_ASM_" + clazz.getSimpleName() + "_" + this.seed.incrementAndGet();
    }

    public String getGenFieldDeserializer(Class<?> clazz, FieldInfo fieldInfo) {
        String name = "Fastjson_ASM__Field_" + clazz.getSimpleName();
        return name + "_" + fieldInfo.getName() + "_" + this.seed.incrementAndGet();
    }

    public ASMDeserializerFactory() {
        this.seed = new AtomicLong();
        this.classLoader = new ASMClassLoader();
    }

    public ASMDeserializerFactory(ClassLoader parentClassLoader) {
        this.seed = new AtomicLong();
        this.classLoader = new ASMClassLoader(parentClassLoader);
    }

    public static final ASMDeserializerFactory getInstance() {
        return instance;
    }

    public boolean isExternalClass(Class<?> clazz) {
        return this.classLoader.isExternalClass(clazz);
    }

    public ObjectDeserializer createJavaBeanDeserializer(ParserConfig config, Class<?> clazz, Type type) throws Exception {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support type :" + clazz.getName());
        }
        String className = getGenClassName(clazz);
        ClassWriter cw = new ClassWriter();
        cw.visit(49, 33, className, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", null);
        DeserializeBeanInfo beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);
        _init(cw, new Context(className, config, beanInfo, 3));
        _createInstance(cw, new Context(className, config, beanInfo, 3));
        _deserialze(cw, new Context(className, config, beanInfo, 4));
        _deserialzeArrayMapping(cw, new Context(className, config, beanInfo, 4));
        byte[] code = cw.toByteArray();
        if (JSON.DUMP_CLASS != null) {
            FileOutputStream fos = null;
            try {
                try {
                    FileOutputStream fos2 = new FileOutputStream(JSON.DUMP_CLASS + File.separator + className + ".class");
                    try {
                        fos2.write(code);
                        if (fos2 != null) {
                            fos2.close();
                        }
                    } catch (Exception e) {
                        ex = e;
                        fos = fos2;
                        System.err.println("FASTJSON dump class:" + className + "失败:" + ex.getMessage());
                        if (fos != null) {
                            fos.close();
                        }
                        Class<?> exampleClass = this.classLoader.defineClassPublic(className, code, 0, code.length);
                        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class);
                        Object instance2 = constructor.newInstance(config, clazz);
                        return (ObjectDeserializer) instance2;
                    } catch (Throwable th) {
                        th = th;
                        fos = fos2;
                        if (fos != null) {
                            fos.close();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    ex = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        Class<?> exampleClass2 = this.classLoader.defineClassPublic(className, code, 0, code.length);
        Constructor<?> constructor2 = exampleClass2.getConstructor(ParserConfig.class, Class.class);
        Object instance22 = constructor2.newInstance(config, clazz);
        return (ObjectDeserializer) instance22;
    }

    void _setFlag(MethodVisitor mw, Context context, int i) {
        String varName = "_asm_flag_" + (i / 32);
        mw.visitVarInsn(21, context.var(varName));
        mw.visitLdcInsn(Integer.valueOf(1 << i));
        mw.visitInsn(128);
        mw.visitVarInsn(54, context.var(varName));
    }

    void _isFlag(MethodVisitor mw, Context context, int i, Label label) {
        mw.visitVarInsn(21, context.var("_asm_flag_" + (i / 32)));
        mw.visitLdcInsn(Integer.valueOf(1 << i));
        mw.visitInsn(126);
        mw.visitJumpInsn(Opcodes.IFEQ, label);
    }

    void _deserialzeArrayMapping(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(1, "deserialzeArrayMapping", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        defineVarLexer(context, mw);
        _createInstance(context, mw);
        List<FieldInfo> sortedFieldInfoList = context.getBeanInfo().getSortedFieldList();
        int fieldListSize = sortedFieldInfoList.size();
        int i = 0;
        while (i < fieldListSize) {
            boolean last = i == fieldListSize + (-1);
            char seperator = last ? ']' : ',';
            FieldInfo fieldInfo = sortedFieldInfoList.get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();
            if (fieldClass == Byte.TYPE || fieldClass == Short.TYPE || fieldClass == Integer.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanInt", "(C)I");
                mw.visitVarInsn(54, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == Long.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanLong", "(C)J");
                mw.visitVarInsn(55, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == Boolean.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanBoolean", "(C)Z");
                mw.visitVarInsn(54, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == Float.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFloat", "(C)F");
                mw.visitVarInsn(56, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == Double.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanDouble", "(C)D");
                mw.visitVarInsn(57, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == Character.TYPE) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanString", "(C)Ljava/lang/String;");
                mw.visitInsn(3);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
                mw.visitVarInsn(54, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanString", "(C)Ljava/lang/String;");
                mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
                mw.visitVarInsn(25, 1);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getSymbolTable", "()Lcom/alibaba/fastjson/parser/SymbolTable;");
                mw.visitVarInsn(16, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanEnum", "(Ljava/lang/Class;Lcom/alibaba/fastjson/parser/SymbolTable;C)Ljava/lang/Enum;");
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
                mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Class<?> itemClass = getCollectionItemClass(fieldType);
                if (itemClass == String.class) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
                    mw.visitVarInsn(16, seperator);
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanStringArray", "(Ljava/lang/Class;C)Ljava/util/Collection;");
                    mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
                } else {
                    mw.visitVarInsn(25, 1);
                    if (i == 0) {
                        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                    } else {
                        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                    }
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");
                    _newCollection(mw, fieldClass);
                    mw.visitInsn(89);
                    mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
                    _getCollectionFieldItemDeser(context, mw, fieldInfo, itemClass);
                    mw.visitVarInsn(25, 1);
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemClass)));
                    mw.visitVarInsn(25, 3);
                    mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "parseArray", "(Ljava/util/Collection;Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)V");
                }
            } else {
                mw.visitVarInsn(25, 1);
                if (i == 0) {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                } else {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                }
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");
                _deserObject(context, mw, fieldInfo, fieldClass);
                mw.visitVarInsn(25, 1);
                if (!last) {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                } else {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "EOF", "I");
                }
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");
            }
            i++;
        }
        _batchSet(context, mw, false);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitVarInsn(25, context.var("instance"));
        mw.visitInsn(Opcodes.ARETURN);
        mw.visitMaxs(5, context.getVariantCount());
        mw.visitEnd();
    }

    void _deserialze(ClassWriter cw, Context context) {
        if (context.getFieldInfoList().size() != 0) {
            for (FieldInfo fieldInfo : context.getFieldInfoList()) {
                Class<?> fieldClass = fieldInfo.getFieldClass();
                Type fieldType = fieldInfo.getFieldType();
                if (fieldClass != Character.TYPE) {
                    if (Collection.class.isAssignableFrom(fieldClass)) {
                        if (fieldType instanceof ParameterizedType) {
                            Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                            if (!(itemType instanceof Class)) {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
            Collections.sort(context.getFieldInfoList());
            MethodVisitor mw = cw.visitMethod(1, "deserialze", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            Label reset_ = new Label();
            Label super_ = new Label();
            Label return_ = new Label();
            Label end_ = new Label();
            defineVarLexer(context, mw);
            _isEnable(context, mw, Feature.SortFeidFastMatch);
            mw.visitJumpInsn(Opcodes.IFEQ, super_);
            Label next_ = new Label();
            mw.visitVarInsn(25, 0);
            mw.visitVarInsn(25, context.var("lexer"));
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "isSupportArrayToBean", "(Lcom/alibaba/fastjson/parser/JSONLexer;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, next_);
            mw.visitVarInsn(25, context.var("lexer"));
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
            mw.visitJumpInsn(Opcodes.IF_ICMPNE, next_);
            mw.visitVarInsn(25, 0);
            mw.visitVarInsn(25, 1);
            mw.visitVarInsn(25, 2);
            mw.visitVarInsn(25, 3);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, context.getClassName(), "deserialzeArrayMapping", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
            mw.visitInsn(Opcodes.ARETURN);
            mw.visitLabel(next_);
            mw.visitVarInsn(25, context.var("lexer"));
            mw.visitLdcInsn(context.getClazz().getName());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanType", "(Ljava/lang/String;)I");
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "NOT_MATCH", "I");
            mw.visitJumpInsn(159, super_);
            mw.visitVarInsn(25, 1);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext", "()Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(58, context.var("mark_context"));
            mw.visitInsn(3);
            mw.visitVarInsn(54, context.var("matchedCount"));
            _createInstance(context, mw);
            mw.visitVarInsn(25, 1);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext", "()Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(58, context.var("context"));
            mw.visitVarInsn(25, 1);
            mw.visitVarInsn(25, context.var("context"));
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(25, 3);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext", "(Lcom/alibaba/fastjson/parser/ParseContext;Ljava/lang/Object;Ljava/lang/Object;)Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(58, context.var("childContext"));
            mw.visitVarInsn(25, context.var("lexer"));
            mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
            mw.visitJumpInsn(159, return_);
            mw.visitInsn(3);
            mw.visitIntInsn(54, context.var("matchStat"));
            int fieldListSize = context.getFieldInfoList().size();
            for (int i = 0; i < fieldListSize; i += 32) {
                mw.visitInsn(3);
                mw.visitVarInsn(54, context.var("_asm_flag_" + (i / 32)));
            }
            for (int i2 = 0; i2 < fieldListSize; i2++) {
                FieldInfo fieldInfo2 = context.getFieldInfoList().get(i2);
                Class<?> fieldClass2 = fieldInfo2.getFieldClass();
                if (fieldClass2 == Boolean.TYPE || fieldClass2 == Byte.TYPE || fieldClass2 == Short.TYPE || fieldClass2 == Integer.TYPE) {
                    mw.visitInsn(3);
                    mw.visitVarInsn(54, context.var(fieldInfo2.getName() + "_asm"));
                } else if (fieldClass2 == Long.TYPE) {
                    mw.visitInsn(9);
                    mw.visitVarInsn(55, context.var(fieldInfo2.getName() + "_asm", 2));
                } else if (fieldClass2 == Float.TYPE) {
                    mw.visitInsn(11);
                    mw.visitVarInsn(56, context.var(fieldInfo2.getName() + "_asm"));
                } else if (fieldClass2 == Double.TYPE) {
                    mw.visitInsn(14);
                    mw.visitVarInsn(57, context.var(fieldInfo2.getName() + "_asm", 2));
                } else {
                    if (fieldClass2 == String.class) {
                        Label flagEnd_ = new Label();
                        _isEnable(context, mw, Feature.InitStringFieldAsEmpty);
                        mw.visitJumpInsn(Opcodes.IFEQ, flagEnd_);
                        _setFlag(mw, context, i2);
                        mw.visitLabel(flagEnd_);
                        mw.visitVarInsn(25, context.var("lexer"));
                        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "stringDefaultValue", "()Ljava/lang/String;");
                    } else {
                        mw.visitInsn(1);
                    }
                    mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass2));
                    mw.visitVarInsn(58, context.var(fieldInfo2.getName() + "_asm"));
                }
            }
            for (int i3 = 0; i3 < fieldListSize; i3++) {
                FieldInfo fieldInfo3 = context.getFieldInfoList().get(i3);
                Class<?> fieldClass3 = fieldInfo3.getFieldClass();
                Type fieldType2 = fieldInfo3.getFieldType();
                Label notMatch_ = new Label();
                if (fieldClass3 == Boolean.TYPE) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldBoolean", "([C)Z");
                    mw.visitVarInsn(54, context.var(fieldInfo3.getName() + "_asm"));
                } else if (fieldClass3 == Byte.TYPE || fieldClass3 == Short.TYPE || fieldClass3 == Integer.TYPE) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldInt", "([C)I");
                    mw.visitVarInsn(54, context.var(fieldInfo3.getName() + "_asm"));
                } else if (fieldClass3 == Long.TYPE) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldLong", "([C)J");
                    mw.visitVarInsn(55, context.var(fieldInfo3.getName() + "_asm", 2));
                } else if (fieldClass3 == Float.TYPE) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldFloat", "([C)F");
                    mw.visitVarInsn(56, context.var(fieldInfo3.getName() + "_asm"));
                } else if (fieldClass3 == Double.TYPE) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldDouble", "([C)D");
                    mw.visitVarInsn(57, context.var(fieldInfo3.getName() + "_asm", 2));
                } else if (fieldClass3 == String.class) {
                    Label notEnd_ = new Label();
                    mw.visitIntInsn(21, context.var("matchStat"));
                    mw.visitInsn(7);
                    mw.visitJumpInsn(Opcodes.IF_ICMPNE, notEnd_);
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "stringDefaultValue", "()Ljava/lang/String;");
                    mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm"));
                    mw.visitJumpInsn(Opcodes.GOTO, notMatch_);
                    mw.visitLabel(notEnd_);
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldString", "([C)Ljava/lang/String;");
                    mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm"));
                } else if (fieldClass3.isEnum()) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitVarInsn(25, 0);
                    mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                    Label enumNull_ = new Label();
                    mw.visitInsn(1);
                    mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass3));
                    mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm"));
                    mw.visitVarInsn(25, 1);
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getSymbolTable", "()Lcom/alibaba/fastjson/parser/SymbolTable;");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldSymbol", "([CLcom/alibaba/fastjson/parser/SymbolTable;)Ljava/lang/String;");
                    mw.visitInsn(89);
                    mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm_enumName"));
                    mw.visitJumpInsn(Opcodes.IFNULL, enumNull_);
                    mw.visitVarInsn(25, context.var(fieldInfo3.getName() + "_asm_enumName"));
                    mw.visitMethodInsn(Opcodes.INVOKESTATIC, ASMUtils.getType(fieldClass3), "valueOf", "(Ljava/lang/String;)" + ASMUtils.getDesc(fieldClass3));
                    mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm"));
                    mw.visitLabel(enumNull_);
                } else {
                    if (Collection.class.isAssignableFrom(fieldClass3)) {
                        mw.visitVarInsn(25, context.var("lexer"));
                        mw.visitVarInsn(25, 0);
                        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo3.getName() + "_asm_prefix__", "[C");
                        Class<?> itemClass = getCollectionItemClass(fieldType2);
                        if (itemClass == String.class) {
                            mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass3)));
                            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldStringArray", "([CLjava/lang/Class;)" + ASMUtils.getDesc((Class<?>) Collection.class));
                            mw.visitVarInsn(58, context.var(fieldInfo3.getName() + "_asm"));
                        } else {
                            _deserialze_list_obj(context, mw, reset_, fieldInfo3, fieldClass3, itemClass, i3);
                            if (i3 == fieldListSize - 1) {
                                _deserialize_endCheck(context, mw, reset_);
                            }
                        }
                    } else {
                        _deserialze_obj(context, mw, reset_, fieldInfo3, fieldClass3, i3);
                        if (i3 == fieldListSize - 1) {
                            _deserialize_endCheck(context, mw, reset_);
                        }
                    }
                }
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                Label flag_ = new Label();
                mw.visitJumpInsn(158, flag_);
                _setFlag(mw, context, i3);
                mw.visitLabel(flag_);
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                mw.visitInsn(89);
                mw.visitVarInsn(54, context.var("matchStat"));
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "NOT_MATCH", "I");
                mw.visitJumpInsn(159, reset_);
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                mw.visitJumpInsn(158, notMatch_);
                mw.visitVarInsn(21, context.var("matchedCount"));
                mw.visitInsn(4);
                mw.visitInsn(96);
                mw.visitVarInsn(54, context.var("matchedCount"));
                mw.visitVarInsn(25, context.var("lexer"));
                mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
                mw.visitJumpInsn(159, end_);
                mw.visitLabel(notMatch_);
                if (i3 == fieldListSize - 1) {
                    mw.visitVarInsn(25, context.var("lexer"));
                    mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
                    mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);
                }
            }
            mw.visitLabel(end_);
            if (!context.getClazz().isInterface() && !Modifier.isAbstract(context.getClazz().getModifiers())) {
                _batchSet(context, mw);
            }
            mw.visitLabel(return_);
            _setContext(context, mw);
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitInsn(Opcodes.ARETURN);
            mw.visitLabel(reset_);
            _batchSet(context, mw);
            mw.visitVarInsn(25, 0);
            mw.visitVarInsn(25, 1);
            mw.visitVarInsn(25, 2);
            mw.visitVarInsn(25, 3);
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "parseRest", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(context.getClazz()));
            mw.visitInsn(Opcodes.ARETURN);
            mw.visitLabel(super_);
            mw.visitVarInsn(25, 0);
            mw.visitVarInsn(25, 1);
            mw.visitVarInsn(25, 2);
            mw.visitVarInsn(25, 3);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "deserialze", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
            mw.visitInsn(Opcodes.ARETURN);
            mw.visitMaxs(5, context.getVariantCount());
            mw.visitEnd();
        }
    }

    private Class<?> getCollectionItemClass(Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            if (actualTypeArgument instanceof Class) {
                Class<?> itemClass = (Class) actualTypeArgument;
                if (Modifier.isPublic(itemClass.getModifiers())) {
                    return itemClass;
                }
                throw new ASMException("can not create ASMParser");
            }
            throw new ASMException("can not create ASMParser");
        }
        return Object.class;
    }

    private void _isEnable(Context context, MethodVisitor mw, Feature feature) {
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/Feature", feature.name(), "Lcom/alibaba/fastjson/parser/Feature;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "isEnabled", "(Lcom/alibaba/fastjson/parser/Feature;)Z");
    }

    private void defineVarLexer(Context context, MethodVisitor mw) {
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getLexer", "()Lcom/alibaba/fastjson/parser/JSONLexer;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, "com/alibaba/fastjson/parser/JSONLexerBase");
        mw.visitVarInsn(58, context.var("lexer"));
    }

    private void _createInstance(Context context, MethodVisitor mw) {
        Constructor<?> defaultConstructor = context.getBeanInfo().getDefaultConstructor();
        if (Modifier.isPublic(defaultConstructor.getModifiers())) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(context.getClazz()));
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(context.getClazz()), "<init>", "()V");
            mw.visitVarInsn(58, context.var("instance"));
            return;
        }
        mw.visitVarInsn(25, 0);
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "createInstance", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;)Ljava/lang/Object;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(context.getClazz()));
        mw.visitVarInsn(58, context.var("instance"));
    }

    private void _batchSet(Context context, MethodVisitor mw) {
        _batchSet(context, mw, true);
    }

    private void _batchSet(Context context, MethodVisitor mw, boolean flag) {
        int size = context.getFieldInfoList().size();
        for (int i = 0; i < size; i++) {
            Label notSet_ = new Label();
            if (flag) {
                _isFlag(mw, context, i, notSet_);
            }
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            _loadAndSet(context, mw, fieldInfo);
            if (flag) {
                mw.visitLabel(notSet_);
            }
        }
    }

    private void _loadAndSet(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.getFieldClass();
        Type fieldType = fieldInfo.getFieldType();
        if (fieldClass == Boolean.TYPE) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(21, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
            return;
        }
        if (fieldClass == Byte.TYPE || fieldClass == Short.TYPE || fieldClass == Integer.TYPE || fieldClass == Character.TYPE) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(21, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
            return;
        }
        if (fieldClass == Long.TYPE) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(22, context.var(fieldInfo.getName() + "_asm", 2));
            if (fieldInfo.getMethod() != null) {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(context.getClazz()), fieldInfo.getMethod().getName(), ASMUtils.getDesc(fieldInfo.getMethod()));
                if (!fieldInfo.getMethod().getReturnType().equals(Void.TYPE)) {
                    mw.visitInsn(87);
                    return;
                }
                return;
            }
            mw.visitFieldInsn(Opcodes.PUTFIELD, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getField().getName(), ASMUtils.getDesc(fieldInfo.getFieldClass()));
            return;
        }
        if (fieldClass == Float.TYPE) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(23, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
            return;
        }
        if (fieldClass == Double.TYPE) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(24, context.var(fieldInfo.getName() + "_asm", 2));
            _set(context, mw, fieldInfo);
            return;
        }
        if (fieldClass == String.class) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
            return;
        }
        if (fieldClass.isEnum()) {
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else {
            if (Collection.class.isAssignableFrom(fieldClass)) {
                mw.visitVarInsn(25, context.var("instance"));
                Type itemType = getCollectionItemClass(fieldType);
                if (itemType == String.class) {
                    mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
                    mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
                } else {
                    mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
                }
                _set(context, mw, fieldInfo);
                return;
            }
            mw.visitVarInsn(25, context.var("instance"));
            mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        }
    }

    private void _set(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        if (fieldInfo.getMethod() != null) {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getMethod().getName(), ASMUtils.getDesc(fieldInfo.getMethod()));
            if (!fieldInfo.getMethod().getReturnType().equals(Void.TYPE)) {
                mw.visitInsn(87);
                return;
            }
            return;
        }
        mw.visitFieldInsn(Opcodes.PUTFIELD, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getField().getName(), ASMUtils.getDesc(fieldInfo.getFieldClass()));
    }

    private void _setContext(Context context, MethodVisitor mw) {
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, context.var("context"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext", "(Lcom/alibaba/fastjson/parser/ParseContext;)V");
        Label endIf_ = new Label();
        mw.visitVarInsn(25, context.var("childContext"));
        mw.visitJumpInsn(Opcodes.IFNULL, endIf_);
        mw.visitVarInsn(25, context.var("childContext"));
        mw.visitVarInsn(25, context.var("instance"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParseContext", "setObject", "(Ljava/lang/Object;)V");
        mw.visitLabel(endIf_);
    }

    private void _deserialize_endCheck(Context context, MethodVisitor mw, Label reset_) {
        Label _end_if = new Label();
        mw.visitIntInsn(21, context.var("matchedCount"));
        mw.visitJumpInsn(158, reset_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACE", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitLabel(_end_if);
    }

    private void _deserialze_list_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo, Class<?> fieldClass, Class<?> itemType, int i) {
        Label matched_ = new Label();
        Label _end_if = new Label();
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "matchField", "([C)Z");
        mw.visitJumpInsn(Opcodes.IFNE, matched_);
        mw.visitInsn(1);
        mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);
        mw.visitLabel(matched_);
        _setFlag(mw, context, i);
        Label valueNotNull_ = new Label();
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "NULL", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, valueNotNull_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitInsn(1);
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
        mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
        mw.visitLabel(valueNotNull_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);
        _getCollectionFieldItemDeser(context, mw, fieldInfo, itemType);
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "getFastMatchToken", "()I");
        mw.visitVarInsn(54, context.var("fastMatchToken"));
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitVarInsn(21, context.var("fastMatchToken"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        _newCollection(mw, fieldClass);
        mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext", "()Lcom/alibaba/fastjson/parser/ParseContext;");
        mw.visitVarInsn(58, context.var("listContext"));
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
        mw.visitLdcInsn(fieldInfo.getName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext", "(Ljava/lang/Object;Ljava/lang/Object;)Lcom/alibaba/fastjson/parser/ParseContext;");
        mw.visitInsn(87);
        Label loop_ = new Label();
        Label loop_end_ = new Label();
        mw.visitInsn(3);
        mw.visitVarInsn(54, context.var("i"));
        mw.visitLabel(loop_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
        mw.visitJumpInsn(159, loop_end_);
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitVarInsn(25, 1);
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemType)));
        mw.visitVarInsn(21, context.var("i"));
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "deserialze", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitVarInsn(58, context.var("list_item_value"));
        mw.visitIincInsn(context.var("i"), 1);
        mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
        mw.visitVarInsn(25, context.var("list_item_value"));
        if (fieldClass.isInterface()) {
            mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, ASMUtils.getType(fieldClass), ProductAction.ACTION_ADD, "(Ljava/lang/Object;)Z");
        } else {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(fieldClass), ProductAction.ACTION_ADD, "(Ljava/lang/Object;)Z");
        }
        mw.visitInsn(87);
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, context.var(fieldInfo.getName() + "_asm"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "checkListResolve", "(Ljava/util/Collection;)V");
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, loop_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitVarInsn(21, context.var("fastMatchToken"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitJumpInsn(Opcodes.GOTO, loop_);
        mw.visitLabel(loop_end_);
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, context.var("listContext"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext", "(Lcom/alibaba/fastjson/parser/ParseContext;)V");
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitLabel(_end_if);
    }

    private void _getCollectionFieldItemDeser(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> itemType) {
        Label notNull_ = new Label();
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);
        mw.visitVarInsn(25, 0);
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getConfig", "()Lcom/alibaba/fastjson/parser/ParserConfig;");
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemType)));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParserConfig", "getDeserializer", "(Ljava/lang/reflect/Type;)Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitLabel(notNull_);
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
    }

    private void _newCollection(MethodVisitor mw, Class<?> fieldClass) {
        if (fieldClass.isAssignableFrom(ArrayList.class)) {
            mw.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(LinkedList.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(LinkedList.class));
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(LinkedList.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(HashSet.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(HashSet.class));
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(HashSet.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(TreeSet.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(TreeSet.class));
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(TreeSet.class), "<init>", "()V");
        } else {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(fieldClass));
            mw.visitInsn(89);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(fieldClass), "<init>", "()V");
        }
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
    }

    private void _deserialze_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo, Class<?> fieldClass, int i) {
        Label matched_ = new Label();
        Label _end_if = new Label();
        mw.visitVarInsn(25, context.var("lexer"));
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "matchField", "([C)Z");
        mw.visitJumpInsn(Opcodes.IFNE, matched_);
        mw.visitInsn(1);
        mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
        mw.visitJumpInsn(Opcodes.GOTO, _end_if);
        mw.visitLabel(matched_);
        _setFlag(mw, context, i);
        mw.visitVarInsn(21, context.var("matchedCount"));
        mw.visitInsn(4);
        mw.visitInsn(96);
        mw.visitVarInsn(54, context.var("matchedCount"));
        _deserObject(context, mw, fieldInfo, fieldClass);
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getResolveStatus", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/DefaultJSONParser", "NeedToResolve", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, _end_if);
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getLastResolveTask", "()Lcom/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask;");
        mw.visitVarInsn(58, context.var("resolveTask"));
        mw.visitVarInsn(25, context.var("resolveTask"));
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext", "()Lcom/alibaba/fastjson/parser/ParseContext;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask", "setOwnerContext", "(Lcom/alibaba/fastjson/parser/ParseContext;)V");
        mw.visitVarInsn(25, context.var("resolveTask"));
        mw.visitVarInsn(25, 0);
        mw.visitLdcInsn(fieldInfo.getName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "getFieldDeserializer", "(Ljava/lang/String;)Lcom/alibaba/fastjson/parser/deserializer/FieldDeserializer;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask", "setFieldDeserializer", "(Lcom/alibaba/fastjson/parser/deserializer/FieldDeserializer;)V");
        mw.visitVarInsn(25, 1);
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/DefaultJSONParser", "NONE", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setResolveStatus", "(I)V");
        mw.visitLabel(_end_if);
    }

    private void _deserObject(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        _getFieldDeser(context, mw, fieldInfo);
        mw.visitVarInsn(25, 1);
        if (fieldInfo.getFieldType() instanceof Class) {
            mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldInfo.getFieldClass())));
        } else {
            mw.visitVarInsn(25, 0);
            mw.visitLdcInsn(fieldInfo.getName());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "getFieldType", "(Ljava/lang/String;)Ljava/lang/reflect/Type;");
        }
        mw.visitLdcInsn(fieldInfo.getName());
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "deserialze", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
        mw.visitVarInsn(58, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _getFieldDeser(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        Label notNull_ = new Label();
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);
        mw.visitVarInsn(25, 0);
        mw.visitVarInsn(25, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getConfig", "()Lcom/alibaba/fastjson/parser/ParserConfig;");
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldInfo.getFieldClass())));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParserConfig", "getDeserializer", "(Ljava/lang/reflect/Type;)Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitLabel(notNull_);
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();
        if (fieldClass == Integer.TYPE || fieldClass == Long.TYPE || fieldClass == String.class) {
            return createStringFieldDeserializer(mapping, clazz, fieldInfo);
        }
        return mapping.createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
    }

    public FieldDeserializer createStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) throws Exception {
        Class<?> superClass;
        int INVAKE_TYPE;
        Class<?> fieldClass = fieldInfo.getFieldClass();
        Method method = fieldInfo.getMethod();
        String className = getGenFieldDeserializer(clazz, fieldInfo);
        ClassWriter cw = new ClassWriter();
        if (fieldClass == Integer.TYPE) {
            superClass = IntegerFieldDeserializer.class;
        } else if (fieldClass == Long.TYPE) {
            superClass = LongFieldDeserializer.class;
        } else {
            superClass = StringFieldDeserializer.class;
        }
        if (clazz.isInterface()) {
            INVAKE_TYPE = Opcodes.INVOKEINTERFACE;
        } else {
            INVAKE_TYPE = Opcodes.INVOKEVIRTUAL;
        }
        cw.visit(49, 33, className, ASMUtils.getType(superClass), null);
        MethodVisitor mw = cw.visitMethod(1, "<init>", "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;Lcom/alibaba/fastjson/util/FieldInfo;)V", null, null);
        mw.visitVarInsn(25, 0);
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, 2);
        mw.visitVarInsn(25, 3);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(superClass), "<init>", "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;Lcom/alibaba/fastjson/util/FieldInfo;)V");
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(4, 6);
        mw.visitEnd();
        if (method != null) {
            if (fieldClass == Integer.TYPE) {
                MethodVisitor mw2 = cw.visitMethod(1, "setValue", "(Ljava/lang/Object;I)V", null, null);
                mw2.visitVarInsn(25, 1);
                mw2.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass()));
                mw2.visitVarInsn(21, 2);
                mw2.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(), ASMUtils.getDesc(method));
                mw2.visitInsn(Opcodes.RETURN);
                mw2.visitMaxs(3, 3);
                mw2.visitEnd();
            } else if (fieldClass == Long.TYPE) {
                MethodVisitor mw3 = cw.visitMethod(1, "setValue", "(Ljava/lang/Object;J)V", null, null);
                mw3.visitVarInsn(25, 1);
                mw3.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass()));
                mw3.visitVarInsn(22, 2);
                mw3.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(), ASMUtils.getDesc(method));
                mw3.visitInsn(Opcodes.RETURN);
                mw3.visitMaxs(3, 4);
                mw3.visitEnd();
            } else {
                MethodVisitor mw4 = cw.visitMethod(1, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
                mw4.visitVarInsn(25, 1);
                mw4.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass()));
                mw4.visitVarInsn(25, 2);
                mw4.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass));
                mw4.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(), ASMUtils.getDesc(method));
                mw4.visitInsn(Opcodes.RETURN);
                mw4.visitMaxs(3, 3);
                mw4.visitEnd();
            }
        }
        byte[] code = cw.toByteArray();
        Class<?> exampleClass = this.classLoader.defineClassPublic(className, code, 0, code.length);
        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class, FieldInfo.class);
        Object instance2 = constructor.newInstance(mapping, clazz, fieldInfo);
        return (FieldDeserializer) instance2;
    }

    static class Context {
        private final DeserializeBeanInfo beanInfo;
        private String className;
        private Class<?> clazz;
        private List<FieldInfo> fieldInfoList;
        private int variantIndex;
        private Map<String, Integer> variants = new HashMap();

        public Context(String className, ParserConfig config, DeserializeBeanInfo beanInfo, int initVariantIndex) {
            this.variantIndex = 5;
            this.className = className;
            this.clazz = beanInfo.getClazz();
            this.variantIndex = initVariantIndex;
            this.beanInfo = beanInfo;
            this.fieldInfoList = new ArrayList(beanInfo.getFieldList());
        }

        public String getClassName() {
            return this.className;
        }

        public List<FieldInfo> getFieldInfoList() {
            return this.fieldInfoList;
        }

        public DeserializeBeanInfo getBeanInfo() {
            return this.beanInfo;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public int getVariantCount() {
            return this.variantIndex;
        }

        public int var(String name, int increment) {
            Integer i = this.variants.get(name);
            if (i == null) {
                this.variants.put(name, Integer.valueOf(this.variantIndex));
                this.variantIndex += increment;
            }
            Integer i2 = this.variants.get(name);
            return i2.intValue();
        }

        public int var(String name) {
            Integer i = this.variants.get(name);
            if (i == null) {
                Map<String, Integer> map = this.variants;
                int i2 = this.variantIndex;
                this.variantIndex = i2 + 1;
                map.put(name, Integer.valueOf(i2));
            }
            Integer i3 = this.variants.get(name);
            return i3.intValue();
        }
    }

    private void _init(ClassWriter cw, Context context) {
        int size = context.getFieldInfoList().size();
        for (int i = 0; i < size; i++) {
            FieldVisitor fw = cw.visitField(1, context.getFieldInfoList().get(i).getName() + "_asm_prefix__", "[C");
            fw.visitEnd();
        }
        int size2 = context.getFieldInfoList().size();
        for (int i2 = 0; i2 < size2; i2++) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i2);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            if (!fieldClass.isPrimitive() && !fieldClass.isEnum()) {
                if (Collection.class.isAssignableFrom(fieldClass)) {
                    FieldVisitor fw2 = cw.visitField(1, fieldInfo.getName() + "_asm_list_item_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
                    fw2.visitEnd();
                } else {
                    FieldVisitor fw3 = cw.visitField(1, fieldInfo.getName() + "_asm_deser__", "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
                    fw3.visitEnd();
                }
            }
        }
        MethodVisitor mw = cw.visitMethod(1, "<init>", "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;)V", null, null);
        mw.visitVarInsn(25, 0);
        mw.visitVarInsn(25, 1);
        mw.visitVarInsn(25, 2);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "<init>", "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;)V");
        mw.visitVarInsn(25, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "serializer", "Lcom/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer$InnerJavaBeanDeserializer;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/JavaBeanDeserializer", "getFieldDeserializerMap", "()Ljava/util/Map;");
        mw.visitInsn(87);
        int size3 = context.getFieldInfoList().size();
        for (int i3 = 0; i3 < size3; i3++) {
            FieldInfo fieldInfo2 = context.getFieldInfoList().get(i3);
            mw.visitVarInsn(25, 0);
            mw.visitLdcInsn("\"" + fieldInfo2.getName() + "\":");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C");
            mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo2.getName() + "_asm_prefix__", "[C");
        }
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();
    }

    private void _createInstance(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(1, "createInstance", "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;)Ljava/lang/Object;", null, null);
        mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(context.getClazz()));
        mw.visitInsn(89);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(context.getClazz()), "<init>", "()V");
        mw.visitInsn(Opcodes.ARETURN);
        mw.visitMaxs(3, 3);
        mw.visitEnd();
    }
}
