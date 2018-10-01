// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: target_agent_prot_simpleRemoteShell_plugin.proto

package com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell;

public final class SimpleRemoteShell {
  private SimpleRemoteShell() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface CommandOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required string cmdWithArgs = 1;
    boolean hasCmdWithArgs();
    String getCmdWithArgs();
  }
  public static final class Command extends
      com.google.protobuf.GeneratedMessage
      implements CommandOrBuilder {
    // Use Command.newBuilder() to construct.
    private Command(Builder builder) {
      super(builder);
    }
    private Command(boolean noInit) {}
    
    private static final Command defaultInstance;
    public static Command getDefaultInstance() {
      return defaultInstance;
    }
    
    public Command getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required string cmdWithArgs = 1;
    public static final int CMDWITHARGS_FIELD_NUMBER = 1;
    private java.lang.Object cmdWithArgs_;
    public boolean hasCmdWithArgs() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public String getCmdWithArgs() {
      java.lang.Object ref = cmdWithArgs_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          cmdWithArgs_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getCmdWithArgsBytes() {
      java.lang.Object ref = cmdWithArgs_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        cmdWithArgs_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    private void initFields() {
      cmdWithArgs_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasCmdWithArgs()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getCmdWithArgsBytes());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getCmdWithArgsBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.CommandOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_fieldAccessorTable;
      }
      
      // Construct using com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        cmdWithArgs_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.getDescriptor();
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command getDefaultInstanceForType() {
        return com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.getDefaultInstance();
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command build() {
        com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command buildPartial() {
        com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command result = new com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.cmdWithArgs_ = cmdWithArgs_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command) {
          return mergeFrom((com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command other) {
        if (other == com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.getDefaultInstance()) return this;
        if (other.hasCmdWithArgs()) {
          setCmdWithArgs(other.getCmdWithArgs());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCmdWithArgs()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              cmdWithArgs_ = input.readBytes();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required string cmdWithArgs = 1;
      private java.lang.Object cmdWithArgs_ = "";
      public boolean hasCmdWithArgs() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public String getCmdWithArgs() {
        java.lang.Object ref = cmdWithArgs_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          cmdWithArgs_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setCmdWithArgs(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        cmdWithArgs_ = value;
        onChanged();
        return this;
      }
      public Builder clearCmdWithArgs() {
        bitField0_ = (bitField0_ & ~0x00000001);
        cmdWithArgs_ = getDefaultInstance().getCmdWithArgs();
        onChanged();
        return this;
      }
      void setCmdWithArgs(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000001;
        cmdWithArgs_ = value;
        onChanged();
      }
      
      // @@protoc_insertion_point(builder_scope:TargetAgent.Protocol.SimpleRemoteShell.Command)
    }
    
    static {
      defaultInstance = new Command(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:TargetAgent.Protocol.SimpleRemoteShell.Command)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n0target_agent_prot_simpleRemoteShell_pl" +
      "ugin.proto\022&TargetAgent.Protocol.SimpleR" +
      "emoteShell\"\036\n\007Command\022\023\n\013cmdWithArgs\030\001 \002" +
      "(\tBO\n:com.elektrobit.ebrace.targetdata.a" +
      "dapter.simpleremoteshellB\021SimpleRemoteSh" +
      "ell"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_TargetAgent_Protocol_SimpleRemoteShell_Command_descriptor,
              new java.lang.String[] { "CmdWithArgs", },
              com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.class,
              com.elektrobit.ebrace.targetdata.adapter.simpleremoteshell.SimpleRemoteShell.Command.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
