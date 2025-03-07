/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webase.front.channel.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameter;
import org.fisco.bcos.web3j.protocol.core.methods.response.*;
import org.fisco.bcos.web3j.protocol.core.methods.response.BcosBlock.Block;
import org.junit.Ignore;
import org.junit.Test;

public class BasicTest extends TestBase {

  @Ignore
  @Test
  public void pbftViewTest() throws Exception {
    int i = web3j.getPbftView().send().getPbftView().intValue();
    System.out.println(i);
    assertNotNull(i > 0);
  }

  @Test
  public void consensusStatusTest() throws Exception {
    System.out.println(web3j.getConsensusStatus().sendForReturnString());
    assertNotNull(web3j.getConsensusStatus().sendForReturnString());
  }

  @Test
  public void getBlockNumber() throws Exception {
    BcosBlock bcosBlock = web3j.getBlockByNumber(DefaultBlockParameter.valueOf("latest"),true).send();
    System.out.println(bcosBlock.getBlock());
    Block block = bcosBlock.getBlock();
    System.out.println(bcosBlock.getBlock().getNonce());
    System.out.println(web3j.getBlockByNumber(DefaultBlockParameter.valueOf("latest"),true).send());
    assertNotNull(web3j.getConsensusStatus().sendForReturnString());
  }

  @Test
  public void syncTest() throws Exception {
    System.out.println(web3j.getSyncStatus().send().isSyncing());
    assertNotNull(web3j.getSyncStatus().send().isSyncing());
  }

//  @Test
//  public void versionTest() throws Exception {
//    String web3ClientVersion = web3j.getNodeVersion().sendForReturnString();
//    System.out.println(web3ClientVersion);
//    assertNotNull(web3ClientVersion);
//  }

  // getPeers
  @Ignore
  @Test
  public void peersTest() throws Exception {
    Peers ethPeers = web3j.getPeers().send();
    System.out.println(ethPeers.getPeers().get(0).getNodeID());
    assertNotNull(ethPeers);
  }

  @Test
  public void groupPeersTest() throws Exception {
    GroupPeers groupPeers = web3j.getGroupPeers().send();
    groupPeers.getGroupPeers().stream().forEach(System.out::println);
    assertNotNull(groupPeers.getResult());
  }

  @Test
  public void groupListTest() throws Exception {
    GroupList groupList = web3j.getGroupList().send();
    groupList.getGroupList().stream().forEach(System.out::println);
    assertTrue((groupList.getGroupList().size() > 0));
  }

  @Ignore
  @Test
  public void getTransactionByBlockNumberAndIndexTest() throws IOException {
    Transaction transaction =
        web3j
            .getTransactionByBlockNumberAndIndex(
                DefaultBlockParameter.valueOf(new BigInteger("1")), new BigInteger("0"))
            .send()
            .getTransaction()
            .get();
    assertTrue(transaction.getBlockNumber().intValue() == 1);
  }

  @Test
  public void basicTest() throws Exception {
    try {
      testDeployContract(web3j, credentials);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Execute basic test failed");
    }
  }

  private void testDeployContract(Web3j web3j, Credentials credentials) throws Exception {
    Ok okDemo = Ok.deploy(web3j, credentials, gasPrice, gasLimit).send();
    if (okDemo != null) {
      System.out.println(
          "####get nonce from Block: "
              + web3j
                  .getBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("0")), true)
                  .send()
                  .getBlock()
                  .getNonce());
      System.out.println(
          "####get block number by index from Block: "
              + web3j
                  .getBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("1")), true)
                  .send()
                  .getBlock()
                  .getNumber());

      System.out.println("####contract address is: " + okDemo.getContractAddress());
      // TransactionReceipt receipt = okDemo.trans(new
      // BigInteger("4")).sendAsync().get(60000, TimeUnit.MILLISECONDS);
      TransactionReceipt receipt = okDemo.trans(new BigInteger("4")).send();
      List<Ok.TransEventEventResponse> events = okDemo.getTransEventEvents(receipt);
      events.stream().forEach(System.out::println);

      System.out.println("###callback trans success");

      System.out.println(
          "####get block number from TransactionReceipt: " + receipt.getBlockNumber());
      System.out.println(
          "####get transaction index from TransactionReceipt: " + receipt.getTransactionIndex());
      System.out.println("####get gas used from TransactionReceipt: " + receipt.getGasUsed());
      // System.out.println("####get cumulative gas used from TransactionReceipt: " +
      // receipt.getCumulativeGasUsed());

      BigInteger toBalance = okDemo.get().send();
      System.out.println("============to balance:" + toBalance.intValue());
    }
  }
}
