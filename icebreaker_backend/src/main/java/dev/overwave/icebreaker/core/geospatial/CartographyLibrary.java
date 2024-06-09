package dev.overwave.icebreaker.core.geospatial;

public interface CartographyLibrary {
    String[] IMPASSABLE_AREAS = new String[]{
            // Новая Земля, низ
            "https://yandex.ru/maps/?ll=57.418563%2C72.416354&rl=56.651676%2C73.135475~-0.469165%2C-0.335877~-0" +
            ".227160%2C-0.142466~-0.252400%2C-0.060328~-0.126200%2C-0.331611~-0.088340%2C-0.310328~0.580519%2C-0" +
            ".616281~0.580519%2C-0.317770~0.768232%2C-0.268659~-0.344986%2C-0.095815~-0.609330%2C-0.027012~-0" +
            ".470156%2C-0.073941~-0.839824%2C0.041510~-1.696792%2C0.238220~-1.198898%2C0.450864~-0.605759%2C0" +
            ".261589~-0.164060%2C0.305311~0.302879%2C0.296550~0.656239%2C-0.023240~0.416459%2C0.398710~-0.391219%2C0" +
            ".173589~0.151440%2C0.142136~0.542659%2C0.129911~0.098976%2C0.270650~0.638029%2C0.104635~1.200788%2C0" +
            ".013952~1.125269%2C-0.131781&z=7.2",
            // Новая Земля, верх
            "https://yandex.ru/maps/?ll=63.313893%2C74.320880&rl=56.877956%2C73.527226~-1.170059%2C-0.062384~-1" +
            ".350995%2C0.072432~-0.624657%2C0.226197~0.928062%2C0.252697~0.981604%2C0.560563~0.321252%2C0.216937~-0" +
            ".196321%2C0.269282~0.180272%2C0.120482~0.712095%2C-0.024137~0.303405%2C0.213429~1.106535%2C0.272527~1" +
            ".052993%2C0.289263~1.963208%2C0.317530~1.249314%2C-0.025481~1.463482%2C0.080533~1.606261%2C0.222209~0" +
            ".535420%2C0.185885~1.392093%2C0.256010~0.945909%2C0.012069~0.642504%2C-0.186275~-0.107084%2C-0.284335~-0" +
            ".624657%2C-0.264857~-3.034048%2C-0.457196~-2.766338%2C-0.359379~-1.677650%2C-0.405138~-0.410489%2C-0" +
            ".302829~-1.052993%2C-0.356858~-1.704189%2C-0.652932&z=6.1",
            // Чёшская губа
            "https://yandex.ru/maps/?ll=47.105509%2C67.162956&rl=39.859465%2C65.612217~0.571289%2C0.176517~0" +
            ".285645%2C0.179793~0.736084%2C0.138473~0.725098%2C0.402148~1.076660%2C-0.101034~0.593262%2C-0.198863~0" +
            ".307617%2C0.097419~0.285645%2C0.316198~-0.087920%2C0.136351~0.153838%2C0.115457~-0.351562%2C0.257786~-0" +
            ".417480%2C0.064020~0.208740%2C0.342778~0.056733%2C0.240125~0.047990%2C0.271222~0.221269%2C0.093358~-0" +
            ".092435%2C0.210301~-1.177824%2C0.172740~-0.699683%2C0.056702~0.592249%2C0.200676~2.420160%2C-0.007634~1" +
            ".063432%2C-0.241525~0.179908%2C-0.376808~0.181719%2C-0.348088~-1.272033%2C-0.076051~-0.172155%2C-0" +
            ".152847~-0.210411%2C-0.308689~0.736440%2C-0.353972~0.812953%2C-0.101552~0.793825%2C0.090289~0.325181%2C0" +
            ".213104~0.114770%2C0.277560~-0.038257%2C0.186911~0.841646%2C0.091086~-0.038257%2C0.350092~2.230623%2C0" +
            ".513198~0.047821%2C-3.555440~-10.950956%2C0.612388&z=7.2",
            // Печорская и Хайпудырская губы
            "https://yandex.ru/maps/?ll=57.901143%2C68.022811&rl=50.528942%2C68.257126~0.582275%2C0.226982~0" +
            ".450439%2C0.000000~0.944824%2C0.176755~0.613904%2C0.313536~1.108481%2C0.146277~0.463154%2C-0.158424~-0" +
            ".721412%2C-0.175751~0.388369%2C-0.449202~0.457934%2C-0.172929~0.058283%2C0.148305~0.241456%2C0.153463~0" +
            ".308065%2C0.094626~0.191500%2C-0.015235~0.383000%2C0.103397~0.324717%2C-0.063807~0.333043%2C0.027368~1" +
            ".223766%2C0.469588~1.347127%2C-0.013936~0.554806%2C-0.120751~1.029015%2C-0.072061~0.166522%2C-1" +
            ".595494~-10.374295%2C0.961900&z=7.35",
            // полуостров по о. Вайгач
            "https://yandex.ru/maps/?ll=62.609991%2C69.093878&rl=60.769265%2C68.845259~0.197754%2C0.142399~-0" +
            ".131836%2C0.180621~-0.131836%2C-0.082272~-0.076904%2C0.168130~-0.362549%2C0.213164~-0.192379%2C0" +
            ".150288~0.723353%2C0.285797~0.435045%2C0.049201~1.782625%2C-0.157119~1.183094%2C-0.269529~1.018659%2C-0" +
            ".310460~1.066392%2C-0.166449~0.259966%2C-0.140955~0.880701%2C-0.113032~-0.042443%2C-0.138722~0" +
            ".848869%2C-0.291792~-0.047749%2C-0.141408~0.137941%2C-0.051280~-7.438213%2C0.655829&z=8.05",
            "https://yandex.ru/maps/?ll=72.394451%2C68.431074&rl=68.295963%2C68.178907~0.280151%2C0.118232~0" +
            ".010986%2C0.073078~0.314822%2C0.217972~0.207028%2C0.259278~-0.269165%2C0.057460~-0.241699%2C0.013847~-0" +
            ".182050%2C0.075831~-0.065142%2C0.089660~-0.258179%2C0.142809~-0.060425%2C0.118618~0.120850%2C0.129537~-0" +
            ".153809%2C-0.003855~-0.236206%2C0.044296~-0.379028%2C0.005771~-0.236206%2C-0.175749~-0.170288%2C0" +
            ".048426~-0.137329%2C0.131169~-0.021973%2C0.288416~0.164795%2C0.228296~0.158347%2C0.237089~-0.641108%2C0" +
            ".237606~-0.066136%2C0.292265~0.290940%2C0.283002~0.191500%2C0.188090~1.090716%2C0.270843~0.600146%2C0" +
            ".357414~0.374005%2C0.770579~0.285056%2C0.110806~0.106270%2C0.157517~0.432956%2C0.002443~0.116565%2C-0" +
            ".070982~1.640238%2C0.007356~1.265564%2C-0.192264~0.133217%2C-0.129339~-0.757673%2C-0.966129~-0" +
            ".366348%2C-0.102851~0.741021%2C-0.400782~0.211081%2C-0.247349~-0.060493%2C-0.442031~-0.260118%2C-0" +
            ".130790~0.084551%2C-1.315363~-1.391205%2C-0.862911~-2.589411%2C0.049689&z=7.4",
            // остров Вайгач
            "https://yandex.ru/maps/?ll=59.028854%2C69.998226&rl=58.385235%2C70.397196~0.175184%2C0.083870~0" +
            ".487365%2C0.128987~1.028528%2C-0.319063~0.562339%2C-0.292786~0.144380%2C-0.148468~-0.175239%2C-0" +
            ".101755~-0.291700%2C-0.044505~-0.517419%2C-0.035078~-0.429079%2C0.211773~-0.271330%2C-0.023909~-0" +
            ".384909%2C0.119271~-0.334429%2C0.142230~-0.176680%2C0.111376~0.006310%2C0.104401&z=7.8",
            // остров Колгуев
            "https://yandex.ru/maps/?ll=50.295142%2C68.683982&rl=48.223105%2C68.801687~0.050629%2C0.232758~-0" +
            ".362304%2C0.090151~-0.174639%2C0.324999~0.667779%2C0.246064~0.883515%2C0.169601~0.723561%2C-0.180707~0" +
            ".045210%2C-0.342265~0.072545%2C-0.171210~-0.255050%2C-0.174955~-0.324812%2C-0.191813~0.007248%2C-0" +
            ".136814~-0.724827%2C0.007916~-0.594358%2C0.081634&z=7.6",
            // остров Белый
            "https://yandex.ru/maps/?ll=71.696859%2C72.902343&rl=69.967922%2C73.032721~-0.445959%2C0.210591~0" +
            ".130460%2C0.146823~0.378839%2C0.174388~0.939949%2C0.042593~0.776129%2C-0.285698~-0.283949%2C-0.253724~-1" +
            ".085318%2C-0.051562&z=7.8",
            // Енисейский залив
            "https://yandex.ru/maps/?ll=82.453737%2C71.021332&rl=73.499880%2C71.770058~1.395264%2C0.367788~0" +
            ".120850%2C0.420005~2.702637%2C0.046072~2.417228%2C-0.354009~1.658699%2C-0.493618~0.906937%2C-0.189140~-0" +
            ".456497%2C-0.734435~0.120850%2C-0.626932~-8.503418%2C0.029765~0.560303%2C0.481319~-1.109619%2C0" +
            ".796963&z=7",
            // Скандинавия
            "https://yandex.ru/maps/?ll=26.938415%2C67.739653&rl=15.007757%2C68.941640~4.030040%2C1.265434~4" +
            ".247488%2C0.633589~2.493406%2C0.325819~2.506495%2C-0.098654~2.697765%2C-0.665184~-0.826303%2C-0.442673~2" +
            ".462693%2C-0.423722~3.017002%2C-0.361252~-2.957296%2C-2.056324~-9.306783%2C-0.084784~-7.654177%2C1" +
            ".176437&z=6.6",
            // полуостров Таймыр
            "https://yandex.ru/maps/?ll=99.823626%2C73.815505&rl=80.785578%2C72.532964~-0.482913%2C0.982398~4" +
            ".329561%2C0.211418~2.164781%2C0.282376~-2.331302%2C0.448144~2.414563%2C0.647168~1.998259%2C0.376406~3" +
            ".696779%2C0.334466~0.566173%2C0.302949~3.580214%2C0.241710~3.958865%2C0.101309~0.307617%2C0.435502~0" +
            ".971094%2C0.486843~2.192968%2C0.324532~2.087402%2C-0.298299~-0.483398%2C-0.369348~1.647949%2C-0" +
            ".103923~-0.900879%2C-0.413697~1.296387%2C0.015360~0.285645%2C0.188027~3.361816%2C-0.010097~2.038607%2C-0" +
            ".279317~0.268522%2C-1.005305~-1.224373%2C-0.548063~-1.632072%2C-0.363415~-7.822266%2C-1.472440~-21" +
            ".247559%2C-0.613091&z=6",
            // остров Большевик
            "https://yandex.ru/maps/?ll=105.097064%2C78.357454&rl=99.603899%2C77.921583~1.604004%2C0.241345~3" +
            ".537598%2C0.156765~0.681152%2C0.385158~-1.625977%2C0.459718~-1.538086%2C0.405467~-1.365427%2C-0" +
            ".140568~-0.941702%2C-0.621769~-0.549316%2C-0.739825&z=6",
            // острова О. Революции и Комсомолец
            "https://yandex.ru/maps/?ll=99.472064%2C79.071213&rl=100.087298%2C79.802182~-0.373535%2C-0.629353~-0" +
            ".109863%2C-0.314053~-1.933594%2C-0.051077~-2.834473%2C0.257262~-0.855883%2C0.478072~-2.681714%2C0" +
            ".133882~-0.131836%2C0.375104~0.944824%2C0.394484~1.098633%2C0.518078~2.219238%2C0.318708~1.494141%2C-0" +
            ".381043~1.120605%2C-0.200222~-0.977243%2C-0.149766~0.340036%2C-0.267585~1.867676%2C-0.270692&z=6",
            // Восточное побережье
            "https://yandex.ru/maps/?ll=175.611464%2C67.554048&rl=105.514544%2C72.854307~3.804742%2C0.517577~1" +
            ".710395%2C0.520052~1.230469%2C-0.159349~1.318223%2C-0.155643~1.757923%2C0.138987~3.273952%2C-0.169117~-0" +
            ".065918%2C-0.384127~4.636230%2C-0.256605~0.175781%2C0.820346~4.724121%2C-0.254499~1.267962%2C-0" +
            ".480805~-0.015521%2C-1.530624~1.713867%2C-0.711989~1.713867%2C1.167897~1.296387%2C-0.525947~1.933594%2C0" +
            ".313175~2.702637%2C-0.083012~0.417480%2C0.707491~1.801758%2C0.474265~6.130371%2C-0.474265~2.966309%2C-0" +
            ".357189~0.000000%2C-0.357236~1.434053%2C-0.210988~1.070830%2C-0.502520~3.713379%2C0.157658~2.196527%2C-0" +
            ".056358~1.560797%2C-0.486324~-0.153809%2C-0.739291~2.065430%2C-0.267753~1.647949%2C0.214472~3" +
            ".295898%2C-0.222172~1.406250%2C0.524828~1.076660%2C-0.165920~-0.197754%2C-0.312751~-0.791016%2C-0" +
            ".007686~-0.043945%2C-0.325321~1.054688%2C-0.180245~0.175781%2C-0.261226~1.494141%2C0.253355~-0" +
            ".483398%2C1.074777~2.922363%2C-0.203079~2.658691%2C-0.045398~2.805815%2C-0.529812~0.572074%2C-1" +
            ".320719~-0.109953%2C-1.361599~-27.679557%2C0.024154~-25.204243%2C2.227443~-21.599121%2C3.642632&z=5.35",
            // остров Котельный
            "https://yandex.ru/maps/?ll=146.437078%2C75.017187&rl=137.536198%2C75.951081~-0.811807%2C-0.611140~1" +
            ".188717%2C-0.499996~1.449655%2C-0.206171~0.289931%2C0.304471~2.145489%2C0.022596~0.811807%2C-0.136076~2" +
            ".029517%2C0.345380~0.492883%2C0.359445~-1.478648%2C0.315486~-2.290454%2C0.336478~-0.724827%2C-0" +
            ".421844~-1.681599%2C0.421844&z=5.6",
            // остров Новая Сибирь
            "https://yandex.ru/maps/?ll=155.592544%2C73.838125&rl=146.481212%2C75.558643~-0.351563%2C-0.343832~1" +
            ".429005%2C-0.319845~1.606778%2C-0.210876~1.530265%2C0.190915~0.191283%2C0.247663~-1.492008%2C0.136954~-1" +
            ".606778%2C0.203121&z=5.2",
            // Ляховские острова
            "https://yandex.ru/maps/?ll=143.475869%2C73.223163&rl=141.112931%2C73.841884~-0.608855%2C-0.326069~-0" +
            ".739324%2C-0.099064~0.072115%2C-0.117267~1.073113%2C0.071670~1.696096%2C-0.120797~0.913282%2C-0.062816~0" +
            ".043490%2C0.303563~-0.898786%2C0.326546~-0.768317%2C0.112798~-0.840800%2C0.052110~0.043490%2C0.254147~-0" +
            ".855296%2C0.011816~-0.014497%2C-0.229983~0.275434%2C-0.092104~0.391407%2C0.000000&z=6.6",
    };

    String[] PASSABLE_AREAS_OVERRIDE = new String[]{
            // Залив у Архангельска
            "https://yandex.ru/maps/?ll=44.884995%2C67.871432&rl=38.773653%2C68.389826~2.314650%2C-0.761381~2" +
            ".880824%2C0.120231~0.233130%2C0.524219~-1.248912%2C0.566646~-1.748477%2C0.791670~-2.880824%2C-0.546368~0" +
            ".399652%2C-0.578687&z=6.4",
            // Над Скандинавией
            "https://yandex.ru/maps/?ll=27.159873%2C71.203295&rl=14.613486%2C68.922512~2.744588%2C0.576791~1" +
            ".737834%2C0.719994~2.134270%2C0.065243~2.677742%2C0.801505~1.911621%2C0.085343~3.208008%2C3.088186~-9" +
            ".799805%2C0.077342~-7.053223%2C-1.142707~1.933594%2C-4.240083&z=6",
            // Река к Дудинке
            "https://yandex.ru/maps/?ll=85.983888%2C69.585247&rl=82.598188%2C70.328519~0.133217%2C-0.254006~0" +
            ".228360%2C-0.326197~0.936720%2C-0.141984~1.208496%2C-0.207912~1.021729%2C-0.327459~1.241455%2C0" +
            ".078415~-0.282866%2C0.411623~-1.727632%2C0.345877~-1.131592%2C0.150549~-0.428467%2C0.127114~-0" +
            ".714111%2C0.144848&z=7",
    };


    String[] PASSABLE_AREAS_IF_WATER = new String[]{
            // Залив у Архангельска
            "https://yandex.ru/maps/?ll=41.422244%2C65.926562&rl=37.225467%2C68.516852~6.525879%2C0.367461~0" +
            ".635870%2C-0.987126~0.682490%2C-2.546795~-5.207520%2C-1.772777~-4.482422%2C-0.068617~-3.032227%2C1" +
            ".896385~-0.900879%2C2.090303~5.361328%2C-0.228390~0.527344%2C0.998484&z=6"
    };
}
