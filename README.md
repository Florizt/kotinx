##  架构模式：
```
UI -> BaseActivity<V : ViewDataBinding, VM : BaseViewModel>
    ↓
    ↓
    ↓ 泛型+koin 示例：TestActivity : BaseActivity<ActivityMainBinding, TestViewModel>()
    ↓
    ↓
    ↓
ViewModel -> BaseViewModel
    ↓
    ↓
    ↓构造+koin 示例：TestViewModel(private val testRepository: TestRepository) : BaseViewModel()
    ↓
Repository
    ↓
    ↓
    ↓构造+koin 示例：TestRepository(private val testModel: TestModel)
    ↓
Model
    ↓
    ↓
    ↓构造+koin 示例：TestModel(private val remoteService: ITestRemoteService,private val localService: ITestLocalService) : BaseModel()
    ↓
Service
      localService
            示例：interface ITestLocalService {
                    @L_POST(type = LocalType.SP, key = arrayOf("name"))
                    suspend fun setName(name: String)

                    @L_GET(type = LocalType.SP, key = arrayOf("name"))
                    suspend fun getName(): String
                 }
      remoteService
            示例：interface ITestRemoteService {
                    @GET("get/remote")
                    suspend fun getAge(@Query("uid") uid: String): BaseHttpResult<Int>
                 }


注册：
AppModule.kt 示例：

//注册ViewModel
val viewModelModule = module {
    viewModel { TestViewModel(get()) }
}

//注册Repository
val repositoryModule = module {
    single { TestRepository(get()) }
}

//注册Model
val modelModule = module {
    single { TestModel(get(), get()) }
}

//注册Service
val serviceModule = module {
    //仿Retrofit实现的Localfit，用法保持一致
    single { LocalfitFactory.getService(androidApplication(), ITestLocalService::class.java) }
    single { RetrofitFactory.getService(ITestRemoteService::class.java) }
}

val allModule = listOf(
    viewModelModule,
    repositoryModule,
    modelModule,
    serviceModule
)

使用：
App.kt 示例：
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App.applicationContext)
            modules(allModule)
        }
    }
}
```
---

##  UI层
### 生命周期
如应用需要实现统计、推送、埋点等第三方，需要在BaseActivity的生命周期里进行注册或者初始化的，
但是为了功能隔离和解耦，这些东西的注册和注销最好不要放在BaseActivity里去实现。

现在通过AOP将BaseActivity生命周期对外开放出来。更多生命周期见：ActivityLifeCycle

同理，BaseViewModel生命周期一样开放出来，见：ViewModelLifeCycle，具体实现参考上面，
内部已通过此AOP思想，实现了部分常见功能，往下看：

###  沉浸式状态栏、底部导航栏和软键盘等的处理
内部已通过此AOP思想，用ImmersionBar实现沉浸式状态栏、底部导航栏和软键盘等的处理，具体可见：BarAspect。可通过继承BaseActivity，对以下方法进行重写：
| 方法      | 描述 |
| --------- | -----:|
| immersionBarEnabled| 是否开启沉浸式，默认true|
| isFullScreen|沉浸式下是否全屏，默认false|
| fitsSystemWindows|沉浸式非全屏下是否自动处理安全区，默认true|
| statusBarDarkFont|沉浸式非全屏下状态栏字体是否深色，默认true，vivo手机由于状态栏字体颜色无法修改，所以默认0.2f的透明度|
| statusBarColor|沉浸式非全屏下状态栏背景颜色，默认白色|
| navigationBarColor|沉浸式非全屏下底部导航栏背景颜色，默认黑色|
| keyboardEnable      |    解决EditText与软键盘冲突，true：EditText跟随软键盘弹起，false反之。默认true |
| keyboardMode      |    软键盘模式，默认SOFT_INPUT_ADJUST_RESIZE |
| onKeyboardChange      |    软键盘监听 |


###  事件总线
内部已通过此AOP思想，用EventBus实现事件总线的注册和注销，具体可见：EventBusAspect。可通过继承BaseViewModel，对以下方法进行重写：
| 方法      | 描述 |
| --------- | -----:|
| onMessageEvent| 接收EventBus事件，默认在ThreadMode.MAIN线程，消息体格式统一为：MessageEvent|


###  应用前后台监听
内部已通过此AOP思想，用AppStackManager实现应用前后台监听，具体可见：BackgroundAspect。可通过在Application实现BaseContract.IApplication，对以下方法进行重写：
| 方法      | 描述 |
| --------- | -----:|
| toBackground| 应用到后台|
| toForeground| 应用到前台|

###  应用堆栈管理
内部已通过此AOP思想，用AppStackManager实现应用堆栈管理，具体可见：AppStackAspect。AppStackManager提供了一管理堆栈的方法。

###  全局Context管理
内部已通过此AOP思想，用BaseApp实现全局Context管理，具体可见：ApplicationAspect。无需手动init。

###  适配
内部已通过此AOP思想，用BaseApp实现适配，具体可见：ApplicationAspect。默认适配宽高为：360*640。
可通过在Application的onCreate()方法上使用注解@AutoSize进行自定义适配规则。示例：
```
class App : Application(), BaseContract.IApplication {
    @AutoSize(designWidthInDp = 375, designHeightInDp = 664)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App.applicationContext)
            modules(allModule)
        }
    }
}
```

###  全局异常捕获
内部已通过此AOP思想，用CrashHandler实现全局异常捕获，具体可见：ApplicationAspect。内部默认是将错误日志存储到本地。存储路径为：版本大于安卓Q：Android/data/data/packageName/document/crash/crash_test.trace，小于安卓Q：sd/packageName/document/crash/crash_test.trace。可通过在Application实现BaseContract.IApplication，对以下方法进行重写：
| 方法      | 描述 |
| --------- | -----:|
| crashOpera| 异常捕获拦截|

###  生命周期扩展
如需在生命周期中自定义一些自己业务的操作，可自定义扩展实现。示例：StatisticAspect
```
1、业务module的build.gradle里新增：（如有就省略该操作）
    apply plugin: 'android-aspectjx'

    android {
        aspectjx {
            enabled true
            exclude 'com.google','com.squareup','com.alipay','org.apache'
        }
    }

2、
  @Aspect
  open class StatisticAspect {
      @Before(onActivityCreate)//是什么生命周期就用什么生命周期
      open fun statisticAspect(joinPoint: JoinPoint){
          // ...
      }
  }
```

###  生命周期开放
如果非要在一个生命周期里进行所有操作，也不是不可以，框架内部也进行了兼容。可通过在Application实现BaseContract.IApplication，对以下方法进行重写：
| 方法      |
| --------- |
| onActivityCreate|
| onActivityNewIntent|
| onActivitySaveInstanceState|
| onActivityResult|
| onActivityStart|
| onActivityResume|
| onActivityPause|
| onActivityStop|
| onActivityDestroy|


###  返回键拦截
内部默认不拦截返回键，可通过继承BaseActivity，对以下方法进行重写：
| 方法      | 描述 |
| --------- | -----:|
| interceptKeyBack| 是否拦截返回键，默认false|
| defaultOpera| 拦截后的操作，默认返回，其他操作请重写方法|

###  点击防抖动
框架通过kotlin扩展属性和DataBinding实现了解决点击防抖动问题。如需设置点击事件，可通过在xml里使用onClickCommand属性。示例：
```
xml：
  <Button
     android:layout_width="wrap_content"
     android:layout_height="wrap_content"
     app:onClickCommand="@{viewModel.test()}" />

ViewModel：
fun test() = {

}
```


###  页面状态View
有些页面整体root或者局部container需要显示加载中、无数据、加载异常等多种状态，框架提供了无侵入式的状态View。

在需要显示多状态的容器里添加如下属性，而且这个容器必须只有一个子view为加载成功需要显示的view。

| 属性      | 描述 |
| --------- | -----:|
| status| 容器状态，最好是通过维护ObservableInt或者LiveData进行状态观察|
| view_loading| loading状态的布局id|
| view_nodata| 加载成功后无数据状态的布局id|
| view_error| 加载失败状态的布局id|
| error_click| 加载失败状态点击操作|

```
<FrameLayout
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      app:error_click="@{viewModel.reLoadData()}"
      app:status="@{viewModel.status}"
      app:view_error="@{@layout/view_error}"
      app:view_loading="@{@layout/view_loading}"
      app:view_nodata="@{@layout/view_nodata}">

      <TextView
           android:layout_width="30dp"
           android:layout_height="30dp"
           android:layout_gravity="center"
           android:text="@{String.valueOf(viewModel.status)}" />
</FrameLayout>

fun reLoadData()={

}
```

###  文件系统
框架内部维护了一套文件系统，具体可见：ContextExt，适配安卓Q。
文件目录为：（以保存图片为例）
1、版本大于安卓Q：Android/data/data/packageName/image/test.png
2、版本小于安卓Q：sd/packageName/image/test.png


##  ViewModel层
通过持有Repository，调用操作数据的方法。

###  协程
框架内部通过kotlin扩展属性，使用ViewModelExt对ViewModel里的协程进行一个扩展。包括，主线程协程和子线程协程等。
具体可见：ViewModelExt。示例：
```
launchUI {
    launchWithIO {
        val age = testRepository.getAge()
        val name = testRepository.getName()
    }

    launchWithUI { }
}
```

##  Repository层
通过持有Model，调用Model中的方法，作一层中转，不直接持有Service。示例：
```
class TestRepository(private val testModel: TestModel) {
    suspend fun getAge(): Result<Int> = testModel.getAge()
    suspend fun getName(): Result<String> = testModel.getName()
}
```

##  Model层
通过持有Service，获取本地数据获取网络数据。
鉴于获取网络数据采用的是Retrofit+OKHttp+coroutines。所以需要对Entity进行一层封装处理，获取到数据之后，对异常或者自定义错误码进行处理。所以这里封装BaseModel，所有Model都需要继承BaseModel。

使用时需要将正确的错误码存储在HttpResultCode中。例如：RESULT_NORMAL表示访问正常。

还需要将域名BASE_URL存储在RetrofitFactory中。

示例：

```
class TestModel(private val remoteService: ITestRemoteService,private val localService: ITestLocalService) : BaseModel() {

    suspend fun getAge(): Result<Int> = callRequest {
        handleResponse(remoteService.getAge("aaa"))
    }

    suspend fun getName(): Result<String> = callRequest {
        handleLocalResponse(localService.getName())
    }
}
```

在ViewModel中就可以直接获取到数据：
```
override fun onCreate() {
    super.onCreate()
    println("onCreate")
    launchUI {
        launchWithIO {
            val age = testRepository.getAge()
            val name = testRepository.getName()
            if (age is Result.Success) {
                println("===${age.data}")
            } else if (age is Result.Failed) {

            }
        }
    }
}
```
这就是coroutines协程，用同步的方式写异步代码。

还有更多的kotlin扩展属性都在ext包下。可自行研究。
