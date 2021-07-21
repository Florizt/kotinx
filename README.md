```
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


