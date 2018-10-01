// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: target_agent_prot_posix_msg_queues_plugin.proto

package com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues;

public final class PosixMessageQueuesProto {
  private PosixMessageQueuesProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface MessageOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required string queue_name = 1;
    boolean hasQueueName();
    String getQueueName();
    
    // required string message = 2;
    boolean hasMessage();
    String getMessage();
  }
  public static final class Message extends
      com.google.protobuf.GeneratedMessage
      implements MessageOrBuilder {
    // Use Message.newBuilder() to construct.
    private Message(Builder builder) {
      super(builder);
    }
    private Message(boolean noInit) {}
    
    private static final Message defaultInstance;
    public static Message getDefaultInstance() {
      return defaultInstance;
    }
    
    public Message getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required string queue_name = 1;
    public static final int QUEUE_NAME_FIELD_NUMBER = 1;
    private java.lang.Object queueName_;
    public boolean hasQueueName() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public String getQueueName() {
      java.lang.Object ref = queueName_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          queueName_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getQueueNameBytes() {
      java.lang.Object ref = queueName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        queueName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    // required string message = 2;
    public static final int MESSAGE_FIELD_NUMBER = 2;
    private java.lang.Object message_;
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public String getMessage() {
      java.lang.Object ref = message_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          message_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getMessageBytes() {
      java.lang.Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    private void initFields() {
      queueName_ = "";
      message_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasQueueName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasMessage()) {
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
        output.writeBytes(1, getQueueNameBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getMessageBytes());
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
          .computeBytesSize(1, getQueueNameBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getMessageBytes());
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
    
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseDelimitedFrom(
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
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message prototype) {
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
       implements com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.MessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_fieldAccessorTable;
      }
      
      // Construct using com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.newBuilder()
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
        queueName_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        message_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.getDescriptor();
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message getDefaultInstanceForType() {
        return com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.getDefaultInstance();
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message build() {
        com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message buildPartial() {
        com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message result = new com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.queueName_ = queueName_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.message_ = message_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message) {
          return mergeFrom((com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message other) {
        if (other == com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.getDefaultInstance()) return this;
        if (other.hasQueueName()) {
          setQueueName(other.getQueueName());
        }
        if (other.hasMessage()) {
          setMessage(other.getMessage());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasQueueName()) {
          
          return false;
        }
        if (!hasMessage()) {
          
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
              queueName_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              message_ = input.readBytes();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required string queue_name = 1;
      private java.lang.Object queueName_ = "";
      public boolean hasQueueName() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public String getQueueName() {
        java.lang.Object ref = queueName_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          queueName_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setQueueName(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        queueName_ = value;
        onChanged();
        return this;
      }
      public Builder clearQueueName() {
        bitField0_ = (bitField0_ & ~0x00000001);
        queueName_ = getDefaultInstance().getQueueName();
        onChanged();
        return this;
      }
      void setQueueName(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000001;
        queueName_ = value;
        onChanged();
      }
      
      // required string message = 2;
      private java.lang.Object message_ = "";
      public boolean hasMessage() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public String getMessage() {
        java.lang.Object ref = message_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          message_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setMessage(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        message_ = value;
        onChanged();
        return this;
      }
      public Builder clearMessage() {
        bitField0_ = (bitField0_ & ~0x00000002);
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }
      void setMessage(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000002;
        message_ = value;
        onChanged();
      }
      
      // @@protoc_insertion_point(builder_scope:TargetAgent.Protocol.PosixMessageQueues.Message)
    }
    
    static {
      defaultInstance = new Message(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:TargetAgent.Protocol.PosixMessageQueues.Message)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n/target_agent_prot_posix_msg_queues_plu" +
      "gin.proto\022\'TargetAgent.Protocol.PosixMes" +
      "sageQueues\".\n\007Message\022\022\n\nqueue_name\030\001 \002(" +
      "\t\022\017\n\007message\030\002 \002(\tBR\n7com.elektrobit.ebr" +
      "ace.targetdata.adapter.posixmsgqueuesB\027P" +
      "osixMessageQueuesProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_TargetAgent_Protocol_PosixMessageQueues_Message_descriptor,
              new java.lang.String[] { "QueueName", "Message", },
              com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.class,
              com.elektrobit.ebrace.targetdata.adapter.posixmsgqueues.PosixMessageQueuesProto.Message.Builder.class);
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
