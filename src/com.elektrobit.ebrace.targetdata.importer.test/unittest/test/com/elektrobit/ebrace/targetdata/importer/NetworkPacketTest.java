/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elektrobit.ebrace.targetdata.importer.internal.NetworkPacket;

public class NetworkPacketTest
{

    private static final String PACKET_TAG_CONTENT = "<packet>"
            + "<proto name=\"geninfo\" pos=\"0\" showname=\"General information\" size=\"326\">"
            + "<field name=\"num\" pos=\"0\" show=\"4\" showname=\"Number\" value=\"4\" size=\"326\"/>"
            + "<field name=\"len\" pos=\"0\" show=\"326\" showname=\"Frame Length\" value=\"146\" size=\"326\"/>"
            + "<field name=\"caplen\" pos=\"0\" show=\"326\" showname=\"Captured Length\" value=\"146\" size=\"326\"/>"
            + "<field name=\"timestamp\" pos=\"0\" show=\"Jul 31, 2015 12:30:32.456132000 UTC\" showname=\"Captured Time\" value=\"1438345832.456132000\" size=\"326\"/>"
            + "</proto>"
            + "<proto name=\"frame\" showname=\"Frame 4: 326 bytes on wire (2608 bits), 326 bytes captured (2608 bits)\" size=\"326\" pos=\"0\">"
            + "<field name=\"frame.encap_type\" showname=\"Encapsulation type: Ethernet (1)\" size=\"0\" pos=\"0\" show=\"1\"/>"
            + "<field name=\"frame.time\" showname=\"Arrival Time: Jul 31, 2015 12:30:32.456132000 UTC\" size=\"0\" pos=\"0\" show=\"Jul 31, 2015 12:30:32.456132000 UTC\"/>"
            + "<field name=\"frame.offset_shift\" showname=\"Time shift for this packet: 0.000000000 seconds\" size=\"0\" pos=\"0\" show=\"0.000000000\"/>"
            + "<field name=\"frame.time_epoch\" showname=\"Epoch Time: 1438345832.456132000 seconds\" size=\"0\" pos=\"0\" show=\"1438345832.456132000\"/>"
            + "<field name=\"frame.time_delta\" showname=\"Time delta from previous captured frame: 0.030374000 seconds\" size=\"0\" pos=\"0\" show=\"0.030374000\"/>"
            + "<field name=\"frame.time_delta_displayed\" showname=\"Time delta from previous displayed frame: 0.000000000 seconds\" size=\"0\" pos=\"0\" show=\"0.000000000\"/>"
            + "<field name=\"frame.time_relative\" showname=\"Time since reference or first frame: 0.030466000 seconds\" size=\"0\" pos=\"0\" show=\"0.030466000\"/>"
            + "<field name=\"frame.number\" showname=\"Frame Number: 4\" size=\"0\" pos=\"0\" show=\"4\"/>"
            + "<field name=\"frame.len\" showname=\"Frame Length: 326 bytes (2608 bits)\" size=\"0\" pos=\"0\" show=\"326\"/>"
            + "<field name=\"frame.cap_len\" showname=\"Capture Length: 326 bytes (2608 bits)\" size=\"0\" pos=\"0\" show=\"326\"/>"
            + "<field name=\"frame.marked\" showname=\"Frame is marked: False\" size=\"0\" pos=\"0\" show=\"0\"/>"
            + "<field name=\"frame.ignored\" showname=\"Frame is ignored: False\" size=\"0\" pos=\"0\" show=\"0\"/>"
            + "<field name=\"frame.protocols\" showname=\"Protocols in frame: eth:ethertype:ip:tcp:giop\" size=\"0\" pos=\"0\" show=\"eth:ethertype:ip:tcp:giop\"/>"
            + "</proto>"
            + "<proto name=\"eth\" showname=\"Ethernet II, Src: 00:00:00_00:00:00 (00:00:00:00:00:00), Dst: 00:00:00_00:00:00 (00:00:00:00:00:00)\" size=\"14\" pos=\"0\">"
            + "<field name=\"eth.dst\" showname=\"Destination: 00:00:00_00:00:00 (00:00:00:00:00:00)\" size=\"6\" pos=\"0\" show=\"00:00:00:00:00:00\" value=\"000000000000\">"
            + "<field name=\"eth.dst_resolved\" showname=\"Destination (resolved): 00:00:00_00:00:00\" hide=\"yes\" size=\"6\" pos=\"0\" show=\"00:00:00_00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.addr\" showname=\"Address: 00:00:00_00:00:00 (00:00:00:00:00:00)\" size=\"6\" pos=\"0\" show=\"00:00:00:00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.addr_resolved\" showname=\"Address (resolved): 00:00:00_00:00:00\" hide=\"yes\" size=\"6\" pos=\"0\" show=\"00:00:00_00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.lg\" showname=\".... ..0. .... .... .... .... = LG bit: Globally unique address (factory default)\" size=\"3\" pos=\"0\" show=\"0\" value=\"0\" unmaskedvalue=\"000000\"/>"
            + "<field name=\"eth.ig\" showname=\".... ...0 .... .... .... .... = IG bit: Individual address (unicast)\" size=\"3\" pos=\"0\" show=\"0\" value=\"0\" unmaskedvalue=\"000000\"/>"
            + "</field>"
            + "<field name=\"eth.src\" showname=\"Source: 00:00:00_00:00:00 (00:00:00:00:00:00)\" size=\"6\" pos=\"6\" show=\"00:00:00:00:00:00\" value=\"000000000000\">"
            + "<field name=\"eth.src_resolved\" showname=\"Source (resolved): 00:00:00_00:00:00\" hide=\"yes\" size=\"6\" pos=\"6\" show=\"00:00:00_00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.addr\" showname=\"Address: 00:00:00_00:00:00 (00:00:00:00:00:00)\" size=\"6\" pos=\"6\" show=\"00:00:00:00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.addr_resolved\" showname=\"Address (resolved): 00:00:00_00:00:00\" hide=\"yes\" size=\"6\" pos=\"6\" show=\"00:00:00_00:00:00\" value=\"000000000000\"/>"
            + "<field name=\"eth.lg\" showname=\".... ..0. .... .... .... .... = LG bit: Globally unique address (factory default)\" size=\"3\" pos=\"6\" show=\"0\" value=\"0\" unmaskedvalue=\"000000\"/>"
            + "<field name=\"eth.ig\" showname=\".... ...0 .... .... .... .... = IG bit: Individual address (unicast)\" size=\"3\" pos=\"6\" show=\"0\" value=\"0\" unmaskedvalue=\"000000\"/>"
            + "</field>"
            + "<field name=\"eth.type\" showname=\"Type: IP (0x0800)\" size=\"2\" pos=\"12\" show=\"2048\" value=\"0800\"/>"
            + "</proto>"
            + "<proto name=\"ip\" showname=\"Internet Protocol Version 4, Src: 127.0.0.1 (127.0.0.1), Dst: 127.0.0.1 (127.0.0.1)\" size=\"20\" pos=\"14\">"
            + "<field name=\"ip.version\" showname=\"Version: 4\" size=\"1\" pos=\"14\" show=\"4\" value=\"45\"/>"
            + "<field name=\"ip.hdr_len\" showname=\"Header Length: 20 bytes\" size=\"1\" pos=\"14\" show=\"20\" value=\"45\"/>"
            + "<field name=\"ip.dsfield\" showname=\"Differentiated Services Field: 0x00 (DSCP 0x00: Default; ECN: 0x00: Not-ECT (Not ECN-Capable Transport))\" size=\"1\" pos=\"15\" show=\"0\" value=\"00\">"
            + "<field name=\"ip.dsfield.dscp\" showname=\"0000 00.. = Differentiated Services Codepoint: Default (0x00)\" size=\"1\" pos=\"15\" show=\"0\" value=\"0\" unmaskedvalue=\"00\"/>"
            + "<field name=\"ip.dsfield.ecn\" showname=\".... ..00 = Explicit Congestion Notification: Not-ECT (Not ECN-Capable Transport) (0x00)\" size=\"1\" pos=\"15\" show=\"0\" value=\"0\" unmaskedvalue=\"00\"/>"
            + "</field>"
            + "<field name=\"ip.len\" showname=\"Total Length: 312\" size=\"2\" pos=\"16\" show=\"312\" value=\"0138\"/>"
            + "<field name=\"ip.id\" showname=\"Identification: 0x61e7 (25063)\" size=\"2\" pos=\"18\" show=\"25063\" value=\"61e7\"/>"
            + "<field name=\"ip.flags\" showname=\"Flags: 0x02 (Don&apos;t Fragment)\" size=\"1\" pos=\"20\" show=\"2\" value=\"40\">"
            + "<field name=\"ip.flags.rb\" showname=\"0... .... = Reserved bit: Not set\" size=\"1\" pos=\"20\" show=\"0\" value=\"40\"/>"
            + "<field name=\"ip.flags.df\" showname=\".1.. .... = Don&apos;t fragment: Set\" size=\"1\" pos=\"20\" show=\"1\" value=\"40\"/>"
            + "<field name=\"ip.flags.mf\" showname=\"..0. .... = More fragments: Not set\" size=\"1\" pos=\"20\" show=\"0\" value=\"40\"/>"
            + "</field>"
            + "<field name=\"ip.frag_offset\" showname=\"Fragment offset: 0\" size=\"2\" pos=\"20\" show=\"0\" value=\"4000\"/>"
            + "<field name=\"ip.ttl\" showname=\"Time to live: 64\" size=\"1\" pos=\"22\" show=\"64\" value=\"40\"/>"
            + "<field name=\"ip.proto\" showname=\"Protocol: TCP (6)\" size=\"1\" pos=\"23\" show=\"6\" value=\"06\"/>"
            + "<field name=\"ip.checksum\" showname=\"Header checksum: 0xd9d6 [validation disabled]\" size=\"2\" pos=\"24\" show=\"55766\" value=\"d9d6\">"
            + "<field name=\"ip.checksum_good\" showname=\"Good: False\" size=\"2\" pos=\"24\" show=\"0\" value=\"d9d6\"/>"
            + "<field name=\"ip.checksum_bad\" showname=\"Bad: False\" size=\"2\" pos=\"24\" show=\"0\" value=\"d9d6\"/>"
            + "</field>"
            + "<field name=\"ip.src\" showname=\"Source: 127.0.0.1 (127.0.0.1)\" size=\"4\" pos=\"26\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.addr\" showname=\"Source or Destination Address: 127.0.0.1 (127.0.0.1)\" hide=\"yes\" size=\"4\" pos=\"26\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.src_host\" showname=\"Source Host: 127.0.0.1\" hide=\"yes\" size=\"4\" pos=\"26\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.host\" showname=\"Source or Destination Host: 127.0.0.1\" hide=\"yes\" size=\"4\" pos=\"26\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.dst\" showname=\"Destination: 127.0.0.1 (127.0.0.1)\" size=\"4\" pos=\"30\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.addr\" showname=\"Source or Destination Address: 127.0.0.1 (127.0.0.1)\" hide=\"yes\" size=\"4\" pos=\"30\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.dst_host\" showname=\"Destination Host: 127.0.0.1\" hide=\"yes\" size=\"4\" pos=\"30\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "<field name=\"ip.host\" showname=\"Source or Destination Host: 127.0.0.1\" hide=\"yes\" size=\"4\" pos=\"30\" show=\"127.0.0.1\" value=\"7f000001\"/>"
            + "</proto>"
            + "<proto name=\"tcp\" showname=\"Transmission Control Protocol, Src Port: 52470 (52470), Dst Port: 1050 (1050), Seq: 1, Ack: 1, Len: 260\" size=\"32\" pos=\"34\">"
            + "<field name=\"tcp.srcport\" showname=\"Source Port: 52470 (52470)\" size=\"2\" pos=\"34\" show=\"52470\" value=\"ccf6\"/>"
            + "<field name=\"tcp.dstport\" showname=\"Destination Port: 1050 (1050)\" size=\"2\" pos=\"36\" show=\"1050\" value=\"041a\"/>"
            + "<field name=\"tcp.port\" showname=\"Source or Destination Port: 52470\" hide=\"yes\" size=\"2\" pos=\"34\" show=\"52470\" value=\"ccf6\"/>"
            + "<field name=\"tcp.port\" showname=\"Source or Destination Port: 1050\" hide=\"yes\" size=\"2\" pos=\"36\" show=\"1050\" value=\"041a\"/>"
            + "<field name=\"tcp.stream\" showname=\"Stream index: 0\" size=\"0\" pos=\"34\" show=\"0\"/>"
            + "<field name=\"tcp.len\" showname=\"TCP Segment Len: 260\" size=\"1\" pos=\"46\" show=\"260\" value=\"80\"/>"
            + "<field name=\"tcp.seq\" showname=\"Sequence number: 1    (relative sequence number)\" size=\"4\" pos=\"38\" show=\"1\" value=\"97771c43\"/>"
            + "<field name=\"tcp.nxtseq\" showname=\"Next sequence number: 261    (relative sequence number)\" size=\"0\" pos=\"34\" show=\"261\"/>"
            + "<field name=\"tcp.ack\" showname=\"Acknowledgment number: 1    (relative ack number)\" size=\"4\" pos=\"42\" show=\"1\" value=\"6507ddb5\"/>"
            + "<field name=\"tcp.hdr_len\" showname=\"Header Length: 32 bytes\" size=\"1\" pos=\"46\" show=\"32\" value=\"80\"/>"
            + "<field name=\"tcp.flags\" showname=\".... 0000 0001 1000 = Flags: 0x018 (PSH, ACK)\" size=\"2\" pos=\"46\" show=\"24\" value=\"18\" unmaskedvalue=\"8018\">"
            + "<field name=\"tcp.flags.res\" showname=\"000. .... .... = Reserved: Not set\" size=\"1\" pos=\"46\" show=\"0\" value=\"0\" unmaskedvalue=\"80\"/>"
            + "<field name=\"tcp.flags.ns\" showname=\"...0 .... .... = Nonce: Not set\" size=\"1\" pos=\"46\" show=\"0\" value=\"0\" unmaskedvalue=\"80\"/>"
            + "<field name=\"tcp.flags.cwr\" showname=\".... 0... .... = Congestion Window Reduced (CWR): Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.ecn\" showname=\".... .0.. .... = ECN-Echo: Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.urg\" showname=\".... ..0. .... = Urgent: Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.ack\" showname=\".... ...1 .... = Acknowledgment: Set\" size=\"1\" pos=\"47\" show=\"1\" value=\"1\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.push\" showname=\".... .... 1... = Push: Set\" size=\"1\" pos=\"47\" show=\"1\" value=\"1\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.reset\" showname=\".... .... .0.. = Reset: Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.syn\" showname=\".... .... ..0. = Syn: Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "<field name=\"tcp.flags.fin\" showname=\".... .... ...0 = Fin: Not set\" size=\"1\" pos=\"47\" show=\"0\" value=\"0\" unmaskedvalue=\"18\"/>"
            + "</field>"
            + "<field name=\"tcp.window_size_value\" showname=\"Window size value: 342\" size=\"2\" pos=\"48\" show=\"342\" value=\"0156\"/>"
            + "<field name=\"tcp.window_size\" showname=\"Calculated window size: 43776\" size=\"2\" pos=\"48\" show=\"43776\" value=\"0156\"/>"
            + "<field name=\"tcp.window_size_scalefactor\" showname=\"Window size scaling factor: 128\" size=\"2\" pos=\"48\" show=\"128\" value=\"0156\"/>"
            + "<field name=\"tcp.checksum\" showname=\"Checksum: 0xff2c [validation disabled]\" size=\"2\" pos=\"50\" show=\"65324\" value=\"ff2c\">"
            + "<field name=\"tcp.checksum_good\" showname=\"Good Checksum: False\" size=\"2\" pos=\"50\" show=\"0\" value=\"ff2c\"/>"
            + "<field name=\"tcp.checksum_bad\" showname=\"Bad Checksum: False\" size=\"2\" pos=\"50\" show=\"0\" value=\"ff2c\"/>"
            + "</field>"
            + "<field name=\"tcp.urgent_pointer\" showname=\"Urgent pointer: 0\" size=\"2\" pos=\"52\" show=\"0\" value=\"0000\"/>"
            + "<field name=\"tcp.options\" showname=\"Options: (12 bytes), No-Operation (NOP), No-Operation (NOP), Timestamps\" size=\"12\" pos=\"54\" show=\"01:01:08:0a:00:50:7d:af:00:50:7d:a7\" value=\"0101080a00507daf00507da7\">"
            + "<field name=\"\" show=\"No-Operation (NOP)\" size=\"1\" pos=\"54\" value=\"01\">"
            + "<field name=\"tcp.options.type\" showname=\"Type: 1\" size=\"1\" pos=\"54\" show=\"1\" value=\"01\">"
            + "<field name=\"tcp.options.type.copy\" showname=\"0... .... = Copy on fragmentation: No\" size=\"1\" pos=\"54\" show=\"0\" value=\"0\" unmaskedvalue=\"01\"/>"
            + "<field name=\"tcp.options.type.class\" showname=\".00. .... = Class: Control (0)\" size=\"1\" pos=\"54\" show=\"0\" value=\"0\" unmaskedvalue=\"01\"/>"
            + "<field name=\"tcp.options.type.number\" showname=\"...0 0001 = Number: No-Operation (NOP) (1)\" size=\"1\" pos=\"54\" show=\"1\" value=\"1\" unmaskedvalue=\"01\"/>"
            + "</field>" + "</field>"
            + "<field name=\"\" show=\"No-Operation (NOP)\" size=\"1\" pos=\"55\" value=\"01\">"
            + "<field name=\"tcp.options.type\" showname=\"Type: 1\" size=\"1\" pos=\"55\" show=\"1\" value=\"01\">"
            + "<field name=\"tcp.options.type.copy\" showname=\"0... .... = Copy on fragmentation: No\" size=\"1\" pos=\"55\" show=\"0\" value=\"0\" unmaskedvalue=\"01\"/>"
            + "<field name=\"tcp.options.type.class\" showname=\".00. .... = Class: Control (0)\" size=\"1\" pos=\"55\" show=\"0\" value=\"0\" unmaskedvalue=\"01\"/>"
            + "<field name=\"tcp.options.type.number\" showname=\"...0 0001 = Number: No-Operation (NOP) (1)\" size=\"1\" pos=\"55\" show=\"1\" value=\"1\" unmaskedvalue=\"01\"/>"
            + "</field>" + "</field>"
            + "<field name=\"\" show=\"Timestamps: TSval 5275055, TSecr 5275047\" size=\"10\" pos=\"56\" value=\"080a00507daf00507da7\">"
            + "<field name=\"tcp.option_kind\" showname=\"Kind: Time Stamp Option (8)\" size=\"1\" pos=\"56\" show=\"8\" value=\"08\"/>"
            + "<field name=\"tcp.option_len\" showname=\"Length: 10\" size=\"1\" pos=\"57\" show=\"10\" value=\"0a\"/>"
            + "<field name=\"tcp.options.timestamp.tsval\" showname=\"Timestamp value: 5275055\" size=\"4\" pos=\"58\" show=\"5275055\" value=\"00507daf\"/>"
            + "<field name=\"tcp.options.timestamp.tsecr\" showname=\"Timestamp echo reply: 5275047\" size=\"4\" pos=\"62\" show=\"5275047\" value=\"00507da7\"/>"
            + "</field>" + "</field>"
            + "<field name=\"tcp.analysis\" showname=\"SEQ/ACK analysis\" size=\"0\" pos=\"34\" show=\"\" value=\"\">"
            + "<field name=\"tcp.analysis.initial_rtt\" showname=\"iRTT: 0.000092000 seconds\" size=\"0\" pos=\"34\" show=\"0.000092000\"/>"
            + "<field name=\"tcp.analysis.bytes_in_flight\" showname=\"Bytes in flight: 260\" size=\"0\" pos=\"34\" show=\"260\"/>"
            + "</field>"
            + "<field name=\"tcp.pdu.size\" showname=\"PDU Size: 260\" size=\"260\" pos=\"66\" show=\"260\" value=\"47494f5001000000000000f8000000030000001100000002000200004e454f00000000020014000000000006000000a6000000000000002849444c3a6f6d672e6f72672f53656e64696e67436f6e746578742f436f6465426173653a312e300000000001000000000000006a000102000000000a31302e302e322e313500ea3300000019afabcb0000000002e417a750000000080000000000000000140000000000000200000001000000200000000000010001000000020501000100010020000101090000000100010100000000260000000200020000000000050100000000000004494e49540000000467657400000000000000000c4e616d655365727669636500\"/>"
            + "</proto>" + "<proto name=\"giop\" showname=\"General Inter-ORB Protocol\" size=\"260\" pos=\"66\">"
            + "<field name=\"\" show=\"GIOP Header\" size=\"12\" pos=\"66\" value=\"47494f5001000000000000f8\">"
            + "<field name=\"giop.magic\" showname=\"Magic: GIOP\" size=\"4\" pos=\"66\" show=\"GIOP\" value=\"47494f50\"/>"
            + "<field name=\"\" show=\"Version: 1.0\" size=\"2\" pos=\"70\" value=\"0100\">"
            + "<field name=\"giop.major_version\" showname=\"Major Version: 1\" size=\"1\" pos=\"70\" show=\"1\" value=\"01\"/>"
            + "<field name=\"giop.minor_version\" showname=\"Minor Version: 0\" size=\"1\" pos=\"71\" show=\"0\" value=\"00\"/>"
            + "<field name=\"\" show=\"Byte ordering: big-endian\" size=\"1\" pos=\"72\" value=\"00\"/>" + "</field>"
            + "<field name=\"giop.flags.little_endian\" showname=\".... ...0 = Little Endian: False\" hide=\"yes\" size=\"1\" pos=\"72\" show=\"0\" value=\"0\" unmaskedvalue=\"00\"/>"
            + "<field name=\"giop.type\" showname=\"Message type: Request (0)\" size=\"1\" pos=\"73\" show=\"0\" value=\"00\"/>"
            + "<field name=\"giop.len\" showname=\"Message size: 248\" size=\"4\" pos=\"74\" show=\"248\" value=\"000000f8\"/>"
            + "</field>" + "</proto>" + "<proto name=\"fake-field-wrapper\">"
            + "<field name=\"\" show=\"General Inter-ORB Protocol Request\" size=\"248\" pos=\"78\" value=\"000000030000001100000002000200004e454f00000000020014000000000006000000a6000000000000002849444c3a6f6d672e6f72672f53656e64696e67436f6e746578742f436f6465426173653a312e300000000001000000000000006a000102000000000a31302e302e322e313500ea3300000019afabcb0000000002e417a750000000080000000000000000140000000000000200000001000000200000000000010001000000020501000100010020000101090000000100010100000000260000000200020000000000050100000000000004494e49540000000467657400000000000000000c4e616d655365727669636500\">"
            + "<field name=\"\" show=\"ServiceContextList\" size=\"202\" pos=\"78\" value=\"000000030000001100000002000200004e454f00000000020014000000000006000000a6000000000000002849444c3a6f6d672e6f72672f53656e64696e67436f6e746578742f436f6465426173653a312e300000000001000000000000006a000102000000000a31302e302e322e313500ea3300000019afabcb0000000002e417a75000000008000000000000000014000000000000020000000100000020000000000001000100000002050100010001002000010109000000010001010000000026000000020002\">"
            + "<field name=\"giop.seqlen\" showname=\"Sequence Length: 3\" size=\"4\" pos=\"78\" show=\"3\" value=\"00000003\"/>"
            + "<field name=\"giop.iiop.sc\" showname=\"ServiceContext\" size=\"10\" pos=\"82\" show=\"\" value=\"\">"
            + "<field name=\"giop.iiop.sc.vscid\" showname=\"0000 0000 0000 0000 0000 0000 .... .... = VSCID: 0x00000000\" size=\"4\" pos=\"82\" show=\"0\" value=\"0\" unmaskedvalue=\"00000011\"/>"
            + "<field name=\"giop.iiop.sc.scid\" showname=\".... .... .... .... .... .... 0001 0001 = SCID: Unknown (0x00000011)\" size=\"4\" pos=\"82\" show=\"17\" value=\"11\" unmaskedvalue=\"00000011\"/>"
            + "<field name=\"giop.seqlen\" showname=\"Sequence Length: 2\" size=\"4\" pos=\"86\" show=\"2\" value=\"00000002\"/>"
            + "<field name=\"giop.endianness\" showname=\"Endianness: Big Endian (0)\" size=\"1\" pos=\"90\" show=\"0\" value=\"00\"/>"
            + "<field name=\"giop.context_data\" showname=\"Context Data: .\" size=\"1\" pos=\"91\" show=\".\" value=\"02\"/>"
            + "</field>"
            + "<field name=\"giop.iiop.sc\" showname=\"ServiceContext\" size=\"10\" pos=\"94\" show=\"\" value=\"\">"
            + "<field name=\"giop.iiop.sc.vscid\" showname=\"0000 0000 0100 1110 0100 0101 .... .... = VSCID: 0x00004e45\" size=\"4\" pos=\"94\" show=\"20037\" value=\"4E45\" unmaskedvalue=\"4e454f00\"/>"
            + "<field name=\"giop.iiop.sc.scid\" showname=\".... .... .... .... .... .... 0000 0000 = SCID: 0x00000000\" size=\"4\" pos=\"94\" show=\"0\" value=\"0\" unmaskedvalue=\"4e454f00\"/>"
            + "<field name=\"giop.seqlen\" showname=\"Sequence Length: 2\" size=\"4\" pos=\"98\" show=\"2\" value=\"00000002\"/>"
            + "<field name=\"giop.endianness\" showname=\"Endianness: Big Endian (0)\" size=\"1\" pos=\"102\" show=\"0\" value=\"00\"/>"
            + "<field name=\"giop.context_data\" showname=\"Context Data: .\" size=\"1\" pos=\"103\" show=\".\" value=\"14\"/>"
            + "</field>"
            + "<field name=\"giop.iiop.sc\" showname=\"ServiceContext\" size=\"174\" pos=\"106\" show=\"\" value=\"\">"
            + "<field name=\"giop.iiop.sc.vscid\" showname=\"0000 0000 0000 0000 0000 0000 .... .... = VSCID: 0x00000000\" size=\"4\" pos=\"106\" show=\"0\" value=\"0\" unmaskedvalue=\"00000006\"/>"
            + "<field name=\"giop.iiop.sc.scid\" showname=\".... .... .... .... .... .... 0000 0110 = SCID: SendingContextRunTime (0x00000006)\" size=\"4\" pos=\"106\" show=\"6\" value=\"6\" unmaskedvalue=\"00000006\"/>"
            + "<field name=\"giop.seqlen\" showname=\"Sequence Length: 166\" size=\"4\" pos=\"110\" show=\"166\" value=\"000000a6\"/>"
            + "<field name=\"giop.endianness\" showname=\"Endianness: Big Endian (0)\" size=\"1\" pos=\"114\" show=\"0\" value=\"00\"/>"
            + "<field name=\"giop.context_data\" showname=\"Context Data: ......(IDL:omg.org/SendingContext/CodeBase:1.0............j........10.0.2.15..3...............P........................... ................... ...............&amp;......\" size=\"165\" pos=\"115\" show=\"......(IDL:omg.org/SendingContext/CodeBase:1.0............j........10.0.2.15..3...............P........................... ................... ...............&amp;......\" value=\"0000000000002849444c3a6f6d672e6f72672f53656e64696e67436f6e746578742f436f6465426173653a312e300000000001000000000000006a000102000000000a31302e302e322e313500ea3300000019afabcb0000000002e417a75000000008000000000000000014000000000000020000000100000020000000000001000100000002050100010001002000010109000000010001010000000026000000020002\"/>"
            + "</field>" + "</field>"
            + "<field name=\"giop.request_id\" showname=\"Request id: 5\" size=\"4\" pos=\"282\" show=\"5\" value=\"00000005\"/>"
            + "<field name=\"giop.rsp_expected\" showname=\"Response expected: 1\" size=\"1\" pos=\"286\" show=\"1\" value=\"01\"/>"
            + "<field name=\"giop.objektkey_len\" showname=\"Object Key length: 4\" size=\"4\" pos=\"290\" show=\"4\" value=\"00000004\"/>"
            + "<field name=\"giop.objektkey\" showname=\"Object Key: 494e4954\" size=\"4\" pos=\"294\" show=\"49:4e:49:54\" value=\"494e4954\"/>"
            + "<field name=\"giop.request_op_len\" showname=\"Operation length: 4\" size=\"4\" pos=\"298\" show=\"4\" value=\"00000004\"/>"
            + "<field name=\"giop.request_op\" showname=\"Request operation: get\" size=\"4\" pos=\"302\" show=\"get\" value=\"67657400\"/>"
            + "<field name=\"giop.request_principal_len\" showname=\"Requesting Principal Length: 0\" size=\"4\" pos=\"306\" show=\"0\" value=\"00000000\"/>"
            + "<field name=\"giop.stub_data\" showname=\"Stub data: 0000000c4e616d655365727669636500\" size=\"16\" pos=\"310\" show=\"00:00:00:0c:4e:61:6d:65:53:65:72:76:69:63:65:00\" value=\"0000000c4e616d655365727669636500\"/>"
            + "</field>" + "</proto>" + "</packet>";

    private static NetworkPacket packet = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        packet = new NetworkPacket();
        packet.fromXML( PACKET_TAG_CONTENT );
    }

    @Test
    public void test()
    {
        long comparisonValue = 1438345832456l;
        assertTrue( packet.getTimestamp() == comparisonValue );
    }
}
