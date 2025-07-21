# 边缘音量助手 (Edge Volume Helper)

[![Language](https://img.shields.io/badge/Language-Kotlin-blueviolet.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)]()

一个简单、轻量且无广告的安卓小工具。通过双击屏幕的左侧或右侧边缘，即可随时随地调出系统音量控制条，再也不用费力去按实体按键了。

A simple, lightweight, and ad-free Android utility that allows you to bring up the system volume panel by double-tapping the left or right edge of your screen. No more fumbling for physical buttons!

---

### 设置 (Setting)
#### 允许访问受限制的设置
#### 重要提示：

文中部分步骤仅适用于 Android 13 及更高版本。了解如何查看 Android 版本。
启用受限制的设置后，应用将能够访问敏感信息，而这可能使您的个人数据面临风险。除非您信任该应用的开发者，否则我们不建议您允许访问受限制的设置。

某些合法应用可能会请求您启用受限制的设置。例如，一款专为残障人士设计的应用可能会请求您开启无障碍设置。能够访问无障碍设置后，应用可读取屏幕上的内容并代表您与应用互动。

* 在 Android 设备上打开“设置”应用。
* 点按应用。
* 点按您要开启受限设置的应用。
* 提示：如果没找到该应用，请先点按查看所有应用或应用信息。
* 依次点按“更多”图标 了解详情 然后 允许受限制的设置。
![Demo GIF](https://user-images.githubusercontent.com/24237865/77539672-2d654b80-6ea3-11ea-9956-23e3a939b6a1.gif)

---

### ✨ 功能 (Features)

* **左右开弓**: 同时支持屏幕的左、右边缘触发。
* **精准手势**: 通过双击手势触发，有效防止日常滑动操作中的误触。
* **原生体验**: 直接调用系统原生的音量UI，无任何自定义界面，简洁高效。
* **轻量省电**: 基于无障碍服务实现，后台资源占用极低，几乎不影响续航。
* **纯粹干净**: 无广告，无后台数据上传，仅申请必要的无障碍权限。
* **隐形无扰**: 触发区域完全透明，不遮挡任何屏幕内容。

---

### 🔧 如何使用 (How to Use)

1.  前往 [Releases 页面](https://github.com/gengshenchen/Android-Edge-Volume-Helper/releases) 下载最新的 `.apk` 安装包。
2.  安装应用。
3.  打开“边缘音量助手”应用。
4.  点击 "前往设置开启" 按钮，页面会自动跳转到系统的“无障碍”设置列表。
5.  在列表中找到“边缘音量助手”，点击进入并**开启服务权限**。
6.  完成！现在您可以回到任何界面，通过双击屏幕边缘来控制音量了。

---

### 👨‍💻 如何构建 (For Developers)

如果您想自己编译或修改本项目：

1.  克隆本仓库: `git clone https://github.com/gengshenchen/Android-Edge-Volume-Helper.git`
2.  用 Android Studio 打开项目。
3.  等待 Gradle 同步完成。
4.  直接点击 "Run" 即可编译和安装。

---

### 📝 开源许可 (License)

本项目采用 [Apache 2.0 License](LICENSE) 开源许可。

Copyright 2025, [gengshenchen]

---

### 致谢 (Acknowledgments)

* 感谢 Google 提供的 Android 开发平台。