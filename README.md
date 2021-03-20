# shrinkImage

#### shrinkImage是无侵入式全量处理图片、检查图片的插件，主要提供下面功能：

- 检查大图资源
  - 自定义图片大小
  - 自定义图片宽高
- 检查重复图片资源
  - 通过MD5方式查找重复图片
- 全量压缩图片或转webp
  - 利用pngquant算法压缩png图片
  - 利用guetzli算法压缩jpg图片
  - 利用cwebp算法转webp格式

#### 插件作用范围：

- 主工程中的图片资源
- jar包中的图片资源
- aar中的图片资源
- Library中的图片资源

#### 使用方式：

##### 使用Jar包方式引入

1. 在主工程的build.gradle中添加：

```groovy
buildscript {
  dependencies {
    classpath files('shrinkPlugin.jar')
  }
}
```

2. 在app的build.gradle中添加：

```groovy
apply plugin: 'com.ichoice.plugin'
wmShrink {
  ...
}
```



##### 使用aar方式引入

1. 在主工程build.gradle中添加：

```groovy
buildscript {
  repositories {
    maven {
      url uri('/repo')
    }
  }
  dependencies {
    classpath 'com.ichoice.plugin:shrink:0.0.1'
  }
}
```

2. 在app的build.gradle中添加：

```groovy
apply plugin: 'com.ichoice.plugin'
wmShrink {
  // 是否开启插件功能
  enable = true
  // 设置大图的最小体积
  largeSize = 10 * 10
  // 设置大图的最小宽高
  largeHeight = 10
  largeWidth = 10
  // 图片处理工具的位置
  toolsDir = '/shrinkPlugin/imageTools/'
  // 是否开启图片大小检查
  isCheckSize = true
  // 是否开启图片宽高检查
  isCheckPixels = true
  // 是否开启重复图片检查
  isCheckRepeat = true
  // 是否开启图片全量压缩
  isShrink = true
  // 图片处理方式 Compress：压缩，ConvertWebp：转webp格式
  optimizeType = 'ConvertWebp'
  // 白名单，白名单内的图片不会压缩，不会被检测重复和大图
  whiteList = ['ic_launcher.png', 'ic_logo.png']
}
```

#### 工具下载地址:

[imageTools](https://github.com/Licardo/shrinkImage/releases/tag/1.0.0)

#### 感谢：

[McImge:批量压缩图片](https://github.com/smallSohoSolo/McImage)

[cwebp](https://developers.google.com/speed/webp/docs/cwebp)

[guetzli](https://github.com/google/guetzli)

[pngquant](https://pngquant.org/)

