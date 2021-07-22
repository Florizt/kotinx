```
架构模式：
                            base
                                UI -> BaseActivity<V : ViewDataBinding, VM : BaseViewModel>
                                 ↑    ↓
                                 ↑    ↓
    LiveData 示例：SingleLiveData ↑    ↓ 泛型+koin 示例：TestActivity : BaseActivity<ActivityMainBinding, TestViewModel>()
DataBinding 示例：ObservableField ↑    ↓
                                 ↑    ↓
                                 ↑    ↓
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
                                                   suspend fun getTest(@Query("name") name: String): BaseHttpResult<Int>
                                               }


                                注册：
                                AppModule.kt 示例：
                                val viewModelModule = module {
                                    viewModel { TestViewModel(get()) }
                                }

                                val repositoryModule = module {
                                    single { TestRepository(get()) }
                                }

                                val modelModule = module {
                                    single { TestModel(get(), get()) }
                                }

                                val serviceModule = module {
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
                                        startKoin { modules(allModule) }
                                    }
                                }

                            ext

```
---

```
生命周期扩展：
如应用需要实现统计、推送、埋点等第三方，需要在BaseActivity的生命周期里进行注册或者初始化的，
为了功能隔离，现在通过AOP将BaseActivity生命周期对外开放出来。更多生命周期见：ActivityLifeCycle
内部已通过此AOP思想，用ImmersionBar实现沉浸式状态栏、底部导航栏和软键盘等的处理，具体可见：BarAspect。

自定义示例：StatisticAspect
1、业务module的build.gradle里新增：
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
      @Before(onActivityCreate)
      open fun statisticAspect(joinPoint: JoinPoint){
          // ...
      }
  }

同理，BaseViewModel生命周期一样开放出来，见：ViewModelLifeCycle，具体实现参考上面，
内部已通过此AOP思想，用EventBus实现事件总线，具体可见：EventBusAspect
```




