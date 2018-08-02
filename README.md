# 剧本杀 / Murder Mystery Game

“剧本杀”源自线下游戏“谋杀之谜”，是一类实时角色扮演(LARP)游戏。在剧情的推动下，几个玩家会共同经历一段故事，每个故事背后都隐藏着一个秘密。玩家会扮演故事中的一个角色，通过语音群聊，将自己得到的线索公开给所有其他玩家，也可以像交换情报一样，通过私聊转述给个别人。玩家们通过互动交流、探讨、交换线索，最后共同揭开这个秘密或发现凶手。

## 功能列表
这个示例程序演示了如何使用 声网 Agora 的音频SDK，实现剧本杀中的群聊和私聊功能。

- 玩家加入游戏。使用 声网 SDK 加入指定的语音群聊频道，可以在群聊频道里和其他玩家进行语音交流；
- 玩家在收集线索时加入私聊频道，和私聊频道中的其他玩家进行交流；
- 玩家可以随时静音自己；
- 玩家可以随时在本地静音其他人；
- 可以通过频道内说话者音量的回调事件看到谁在说话；
- 围观模式，只能听群聊语音，不能听私聊，不能说话。

## 注意事项
示例程序只演示了“剧本杀”类场景中和语音聊天相关部分的逻辑，不是完整的产品。

1. 示例程序中和语音聊天无关的控件**不可用**。
2. 示例程序中没有业务部分逻辑。如要开发完整的“剧本杀”产品，需要自行实现业务部分逻辑。比如：选择剧本、阅读剧本、玩家列表、收集线索、文字消息、投票等等。

## 运行示例程序
首先在 [Agora.io 用户注册页](https://dashboard.agora.io/cn/signup/) 注册账号，并创建自己的测试项目，获取到 AppID。并在 [Agora.io SDK 下载页](https://www.agora.io/cn/blog/download/) 下载对应平台的 **语音通话 + 直播 SDK**。

#### iOS
1. 将有效的 AppID 填写进 KeyCenter.swift

	```
	static func appId() -> String {
	    return "YOUR APPID"
	}
	```

2. 解压下载的语音通话 SDK 压缩包，将其中的 libs/**AgoraAudioKit.framework** 复制到本项目的 iOS/ARD-Agora-Murder-Mystery-Game 文件夹下。
3. 使用 XCode 打开 iOS/ARD-Agora-Murder-Mystery-Game.xcodeproj，连接 iOS 测试设备，设置有效的开发者签名后即可运行。

		运行环境:
		* XCode 9.0 +
		* iOS 8.0 +

#### Android
1. 将有效的 AppID 填写进 "app/src/main/res/values/strings_config.xml"

	```
	<string name="private_app_id"><#YOUR APP ID#></string>
	```

2. 解压下载的语音通话 SDK 压缩包，将其中的 **libs** 文件夹下的 ***.jar** 复制到本项目的 **app/libs** 下，其中的 **libs** 文件夹下的 **arm64-v8a**/**x86**/**armeabi-v7a** 复制到本项目的 **app/src/main/jniLibs** 下。
3. 使用 Android Studio 打开该项目，连接 Android 测试设备，编译并运行。也可以使用 `Gradle` 直接编译运行。

		运行环境:
		* Android Studio 2.0 +
		* minSdkVersion 16
		* 部分模拟器会存在功能缺失或者性能问题，所以推荐使用真机 Android 设备，


## 联系我们

- 如果发现了示例程序的 bug，欢迎提交 [issue](https://github.com/AgoraIO-Usecase/Murder-Mystery-Game/issues)
- 声网 SDK 完整 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题，你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题，可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持，你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单

## 代码许可

The MIT License (MIT).
