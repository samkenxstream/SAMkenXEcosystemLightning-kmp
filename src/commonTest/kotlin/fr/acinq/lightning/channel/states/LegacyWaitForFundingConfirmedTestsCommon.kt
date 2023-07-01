package fr.acinq.lightning.channel.states

import fr.acinq.bitcoin.*
import fr.acinq.lightning.Lightning.randomKey
import fr.acinq.lightning.blockchain.BITCOIN_FUNDING_DEPTHOK
import fr.acinq.lightning.blockchain.WatchConfirmed
import fr.acinq.lightning.blockchain.WatchEventConfirmed
import fr.acinq.lightning.blockchain.fee.OnChainFeerates
import fr.acinq.lightning.channel.*
import fr.acinq.lightning.serialization.Encryption.from
import fr.acinq.lightning.tests.TestConstants
import fr.acinq.lightning.utils.MDCLogger
import fr.acinq.lightning.utils.value
import fr.acinq.lightning.wire.ChannelReady
import fr.acinq.lightning.wire.ChannelReestablish
import fr.acinq.lightning.wire.EncryptedChannelData
import fr.acinq.lightning.wire.Init
import org.kodein.log.LoggerFactory
import org.kodein.log.newLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LegacyWaitForFundingConfirmedTestsCommon {
    @Test
    fun `restore legacy channel`() {
        // This data was generated with lightning-kmp v1.1.0
        val waitForFundingConfirmed = ByteVector.fromHex(
            "1908557d740ca5f84607133a624cc2dc68ee86b669f953f950ecefcdb5895ea4d35f5b48095693a87f699eeb2e3e1639bff3df2c760a68ee6d5016266c8901e458e38cb43621197b6f410b3f87518a154b29caa86c17ca32b8c6c82c7d200c06c286cd60c6acb4ee04336484a50e6e44f197ddd3781568138bb40a2928cc279e136c6cff9d34405e3a4498846463ab60597f8900847dc1f874e03003c57d2ba5df52e65407f1ec1a09bf2d94b8f77b09632a722d5b67f60279c303b1cd05e3bceacc01482215b2afaa50d2703089c9cc0fb3aa2ae820b198bb841f51c97e845ed64ac96da96fac4bc396ac30d42348b52856386e594872da3db594c361143d22a9ee797dbea03783ba7437145d2713f39d831ab862f265fca41f0b574edc9b5cd2e7859cb05586276e378f5e3437af680b1276d8a8c7b44a8fd5c087899bf69e3cad48e71a21b1496230c1a3636cccd9919166434ee5b34c55f2cfaf640ae75c834ce5d4d944f46439053b73b9f421356635720b9bda92ffe9f46fd6b0d3e25ce487bffbbf3fe067457cb3baa0462033d7d753d4fcfb224ca3ab6da32e77c3610bc22c8c1017a5cb6c0403a4001a0ef54560fd9491f709dce538e67e7a4635532e4cc21969c8ff1201cf4ab651c457043552a621c3d315e50c59793ebf70d0809dda738bd3f29cc92d42c5c391c7411f4b2fff991c7b3a2a72e36acb59703e2ae2630f94c3c7fd92a9b4ea33e3d85961abce3512fa6fb0140eb6930ba02db6b12a362bdf745b223b0c930a0e82049304e7d018b8d8bc9f7b2e3e03b27a68a6dcf158d16e79b2090d4fcd2e2cc74a0f7de3034e1868cee1ad6f850bf0dd7728bf47a057fa443ff89214653ee56e146f3f16bc4dbcf52e16a173a14c806d54a55743ca39cf04f29895f667de344d26482d51f8c158e8805582272c231f856fd4481a3ed497eb9f9d05c9ba29dffb061356a78bae245626add2c669909d1b32d932b3cb25633647f819cb5c154ace541af17da8736e6ec817c9a1ceb4f4d3168776175ac3a0cb3365bab51a84be269f6d4257b4e48d69202536fe58646f61bf0eb403b656edcc5bd5a6a939c9f2c8adaa600d823408ef31450a0cd5e325264945db439032647367e476b9d02d4377c14ea49e7ff5ed08c9162d148233d6f325668ad4583632a3cbbf812316569856743caee4e4a39c9f90ebef4f610d36a866c5a2063f725e6c29f7b677c7ada795a44530bea415d630d3b21c4cc81954ca8cfe36cfbbdcffa02124cdd277b8c856d5299b9b2aafe682b27e9a12fb03a6fbcab59d1f4f26d5acab518a9bed3c25aebae08aa8f819ebd9908cc516edb9870094b83161dc77a1647c43ae0561ad4db359c7318da6cc8437d31cc610b1c43458ff32e3cf0e701369b3927eb4a163140cf75d63c2729d4852d20c26f59bafe84d30ee3e3ace84b9bd393433d42145db1011c865653247762ad8dea3520d832214219cd0ccff9dbd3959b59b02c30b6d02393f1369e108a99f953fbc4502bff31b59d5238170d5d0d1af201e8c208ffcd5c7d40d5b3c069335c0d7d677390ad925a2cca9c3877108cb2a6101ffbd31d7444672c912f6637c521cd7edb799252290242bc22e5b1a6e61dd72989c776a61648145a21f41d779ed45b6435907e917b990d4b67a9b717713f925afe8849f6c6cd9d54a328c2df7cfd51deb40a09a87a866da84cf0b48bb72f442ef2c4be5bd1b6b4cdaa7d6bd0891d1591933657e498d0021a69dbe58619ccbeed9a53d785f36dca57866df09f293610fa9ab333521f2f072398d3c34a2f1293d71903f5dee7458607a3d3750bc98778ef7f1d894bbee254d8546dc4a073ea1626ac69b025a513bdc28fde81cec9c176569d382cdfaf7115d09fb4943229ee15188a6b8625dafbd5b68669e7ccee85765cbff685c9b35ef54fac07f082ee6359c4fad518fe88258de68a053029724429e42690f8258625ac9a5de155bfc19c10da6254f9d712c787be74930cce228d74f672dfd775ef47764a4d563864abac6e0707a7cbd7a5eed72236a19a2f4d1eeb021a90a58bc849435c7cd9a398c46402d23e6c9982ebdef1dd0b04418090b50efa483ad7655ef5b081fb42f1410168a519d88243e15a856c4d595b620972c7b50d696488e9535b20178492a0f4df6b4d5c57698dafda3d156e8113732175b19c722dd3681ca15df187625517ec6f5e074fe63864d46be96405913f2b90ea5fe6771d1312b44617e7a8af69d6ebdcd88744b0136d375b49abda272fdb97cee6574d589dae485afddd08f2f9610c7671a7e2ad8f9fe3f6664f4f1aa259bfca3f8f369e572fa4fe420af65e215a86146f198111da3660146baae91a3d91dacf68c7920d005b2f3b240919be972e06295f74e0c2bff325b3980f7ba7ee3057da3cd5a78396e8ea0f52a35f951cc85ddf3fbbd55d6af038c7c6ec468bfbd415a456c85ab0acdb01cf7cc2d6bb9c97bb903d67ec88f9371ba00014cd0f600dfa81fe25db9bc9d2bc0906670a208a700d2b9042966c0aef95bf0993bb85f7f8ab94e8b3c3803fb7b5fdf2fd40ca7dd7eca2ecc7dee5257bc57a8054f59113f5ad3a0e77e5eb281968ab0959cea7af88d7c2f7f8e01474c2cfd5cfaab9157616002579b3a50a3e3425deecbf10a2007caa5c4ba0ff80891f4b4620964c1a90d9a9f04676d6d3773ea69b97b98a6c1efadd16d920c71fa42d26d5c54b940da5b948a61bf194373fe63d2d11c61b72abfd4798c4098ad8ee9cc905ffa86e9f8a9db9b7edebb1b9fd0385e24efe6a8d85b3b87c255397f6f9cf6173f03023a2280a9b1a00d776ceaaadfd184aa3d404d5a0ef5f8e1309c1c13de8d417870ffd8d2c97cd0e6a504cb4558b766726ea86279af776ae731db0d7f7beebd246198d3ab7f8d54a5dd74af6effcdd424b1b924eedd2f76cf777c8ecce1ac70d35f75a541cb76a06db6fbcb5e43cc19c36e85968e856500a12179fb45ee111f3e9b0ebe4c626b60843c03fa16faa75bbc2b6fab92e30dad7516680b02d349cf0c15153ab6af22eb7c8cefefaf8056a32e0f2c05ee1584226a74c04f9005ec884add4a18d5420dd252a37eeb3a15827c3315f46bacd4707c01d62fb2e474618dc289ce3be7e4da3e511120da4cd5630a96a31a08f220ba5328e92f06bd3fa738f7e08dea5c569454f66f9ec1606cf292e3f751af2b9d1b71f6c91a171ff52a2656f0b5dc4024507e0eabac730dfc642c14882de8c851f1a7e4705349c210b15b2a20859c004fe9c40a02405e6dcbe3364f827bc0c82270336cd08cc40790cd6b867e35997992d93188ee90e6984e108ca8503268cb81e27b2224706ab0a54dee215a29243f6f58a5e6d80b5de048e3ef905ab25f756b6462c2e59e4ae5742f06d24ee0333ec9a8485f9b0e48d23d9dfa494279cc84f4bb7971d69b3b46802481e4c8bdce37e9717bf57ac5eed94e941f0fcd7565b829dbcb52932792dceefb85a0e22c5da57a23c9fc52a581dc0189c1a9c33801fb97154a609686f740953536611b2f007b0b866646174c1dbc2ac3035edf1a24ab88438fed501287b84204f1a07cbadbdd1f30b53cef251becfcfeea5ef767df19b49e3dec15860e14a58e26988ce541bde95a417c3850f9f8fc290baade17eacb2d408193da1077de4053ccd1d07ac1f2d276a5c62980ca749498f86cf29284b2d8cd600d402ee21fd18a96d87b9415f713b1589e4e46ff4b24861f05660dbabfec82b8635fc253e23c1c70a4f26fe5b9ff87b88693f92f82f57b9ee7a9b6e232983f077b7d1904a123275e150ae4edd9c9bc9738ff4da1e053e334ceb7db7cc0810d9a17afbc86a3e10749bf21eca931e740195c1dc3e8ac9914a7e788da00d654a831f6c44b518011a8f428bccf9ebb35f3e52292126555bca5450df067b061d6062df822d17360bed60433358f9321817b51ef8ed3db203dabb6386e13279e17bd62ba1e688dc724b61e52dba54a57a933efa52f9451e2f71eddbc1f457c476bba1f6fe5ad58456c9af72b6620b54b3f4250c7e62c00992073089e0740489108dcfb66b179cf1dea40e0c253d5b414a35c681472f2ca22c9b8eab39cdce0b46873ac073b5a1819852492706f8d9c00bc54d4add581f441993be20f9c6b79067a214e650adb581db6055d97e8372f9bf5a71769abd6470630ad4fa4eae935d54c24a0a6e38c36475777a0d7a3a4ec9f2a2f6f2bd51830f91be09f3a51cbc238c33352e24e79fd809963b00d5ccd9091425ab221fbb315ea5916e005a8287114266674283624425bcefd0208088cdba34893d4a6f0652b02e61cbf7af17d18347d07ae7032fb4106865529fa419f3f2a4bb33e3f571903844bdfb70587c7c2653630f43a60e88e26d992255bf8c21062b972a6b7351b56b173bdde9a4495dc467ea98eec9d81cecb0b6d264f1b1786d974ffe61d7cc88be65cf6d1f3016444ca7b20e8f0bae9d599149ceda1720b6f96494205c88318d296d85445514267f2692a3b4060db5d28ea878b2aff099ef1ab912152589c8d5c865c79037369a1041af18b26dd5f674dce4713c4f741a516f2abdd28793dea949fc3a9a9d34cd9ce5f46e94ab32f38c06757e2e0bc7cbd6a63dce7bf99cbf6e4543c88ac323ef5101bbd43230c8c8bd5fb46ee1299be0b343aeeea62c5611663389b3773e795b8b6a4067d9f72f2dc0f0f3ce8ba95bdee2c2527ff98ec099ab70668ba8db8b5d7f877cc5b003c3a48fdbff5468a5592e741696dcfb2f3e52fbb7bc44ef4cc80151d9535f431a4f48156185e80f2b967fd5d18ae289b9534b2a8d891166e44e106c37c9e458fa19818bba7949e754e3db17bb1b43731e7a24d4b060601e63a0f1e49eb85c0924b481b8f8ae0520b2f7b8d362bde86bf13f07baa08c45217b5cecffd0042fb617bf36d17484c6272029a70fc2dc9bb95cb510cb679de804bbb0d68ef10dc0a8cfc0f165cf3ae3c2aafc192b2bed799b68276de8cccc146db52446edd7c61cdaf792271705386f77bb0a37bd25018f6da625eee685866d55724a358b4091f85d2e3a1d36b4439a06c3ce7fd0c8b93849a001de77db477e7d2ca967ddd1d26c8ca80e79e82e74dda777ea190295f73106485be6256c61c87df637105a421d61272882072ced3cd029310dfd24f1a32b6817d329df706a19d5e3eb4be3d79bb8fe77264"
        )
        val state = PersistedChannelState.from(TestConstants.Bob.nodeParams.nodePrivateKey, EncryptedChannelData(waitForFundingConfirmed)).getOrNull()!!.value
        assertIs<LegacyWaitForFundingConfirmed>(state)
        val fundingTx = Transaction.read("020000000100000000000000000000000000000000000000000000000000000000000000000000000000ffffffff0140420f0000000000220020f9aafa9be1212d0d373760c279f3817f9be707d674cae5f38bb31c1fd85c202900000000")
        assertEquals(state.commitments.latest.fundingTxId, fundingTx.txid)
        val ctx = ChannelContext(
            StaticParams(TestConstants.Bob.nodeParams, TestConstants.Alice.nodeParams.nodeId),
            TestConstants.defaultBlockHeight ,
            OnChainFeerates(TestConstants.feeratePerKw, TestConstants.feeratePerKw, TestConstants.feeratePerKw, TestConstants.feeratePerKw),
            MDCLogger(LoggerFactory.default.newLogger(ChannelState::class))
        )
        val (state1, actions1) = LNChannel(ctx, WaitForInit).process(ChannelCommand.Init.Restore(state))
        assertIs<LNChannel<Offline>>(state1)
        assertEquals(actions1.size, 1)
        val watchConfirmed = actions1.findWatch<WatchConfirmed>()
        assertEquals(watchConfirmed.event, BITCOIN_FUNDING_DEPTHOK)
        assertEquals(watchConfirmed.txId, fundingTx.txid)
        // Reconnect to our peer.
        val localInit = Init(state.commitments.params.localParams.features)
        val remoteInit = Init(state.commitments.params.remoteParams.features)
        val (state2, actions2) = state1.process(ChannelCommand.Connected(localInit, remoteInit))
        assertIs<LNChannel<Syncing>>(state2)
        assertTrue(actions2.isEmpty())
        val channelReestablish = ChannelReestablish(
            state.channelId,
            state.commitments.remoteCommitIndex + 1,
            state.commitments.localCommitIndex,
            PrivateKey(ByteVector32.Zeroes),
            randomKey().publicKey()
        )
        val (state3, actions3) = state2.process(ChannelCommand.MessageReceived(channelReestablish))
        assertEquals(state, state3.state)
        assertEquals(actions3.size, 1)
        actions3.hasOutgoingMessage<ChannelReestablish>()
        // The funding tx confirms.
        val (state4, actions4) = state3.process(ChannelCommand.WatchReceived(WatchEventConfirmed(state.channelId, watchConfirmed.event, 1105, 3, fundingTx)))
        assertIs<LNChannel<LegacyWaitForFundingLocked>>(state4)
        assertEquals(actions4.size, 2)
        actions4.hasOutgoingMessage<ChannelReady>()
        actions4.has<ChannelAction.Storage.StoreState>()
    }
}