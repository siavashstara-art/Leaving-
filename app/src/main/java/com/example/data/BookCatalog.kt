package com.example.data

import com.example.R
import com.example.model.BookPart
import com.example.model.BookSection
import com.example.model.Chapter
import com.example.model.ContentCategory
import com.example.model.ManuscriptApprovalStatus
import com.example.model.ReaderBlock

object BookCatalog {
  const val BOOK_TITLE_FA = "هنر زیستن"
  const val BOOK_SUBTITLE_FA = "کتاب مصور زندگی"
  const val BOOK_TITLE_EN = "Honar Zistan"
  const val BOOK_SUBTITLE_EN = "The Living Book of Life"
  const val AUTHOR_FA = "سیاوش علی‌میری"
  const val AUTHOR_EN = "Siavash Ali-Miri"

  val parts: List<BookPart> =
    listOf(
      BookPart(
        id = "part_1",
        number = 1,
        titleFa = "دفتر اول: سرآغاز هم‌مسیر، معنا و حضور",
        titleEn = "Part I: The Companion's Opening, Meaning & Presence",
        descriptionFa = "معرفی صدای واقعی مؤلف، پیوند جهان و خویشتن (DOC-007) و قدرت یک کلمه (DOC-005).",
        descriptionEn = "The author's authentic voice, connecting the world to the self, and the quiet power of one word.",
      ),
      BookPart(
        id = "part_2",
        number = 2,
        titleFa = "دفتر دوم: ریشه‌های خاطره، شعر و پناه دل",
        titleEn = "Part II: Roots of Memory, Poetry & Spiritual Refuge",
        descriptionFa = "خاطره‌ی خواب مادر، سوگ و امید در شعر فارسی و پناه جستن دل در آیات الهی.",
        descriptionEn = "A mother's enduring dream, Persian verse rising from sorrow, and Quranic steadiness.",
      ),
      BookPart(
        id = "part_3",
        number = 3,
        titleFa = "دفتر سوم: قدرت سکوت، تندرستی و زیستِ متعادل",
        titleEn = "Part III: Power of Silence, Wellbeing & Balanced Living",
        descriptionFa = "خویشتن‌داری در لحظه خشم (DOC-006)، آیین بامداد مهربان و آرامش جسم و خواب.",
        descriptionEn = "Inner mastery through silence, gentle morning rituals, and physical-emotional equilibrium.",
      ),
      BookPart(
        id = "part_4",
        number = 4,
        titleFa = "دفتر چهارم: از خاکستر تا آفرینش، اخلاق و کتاب زنده",
        titleEn = "Part IV: From Ashes to Creation, Ethics & the Living Book",
        descriptionFa = "برخاستن به یاد عزت پدر، آموختن و فناوری (FB NewMeta / TAVANA)، اخلاق بقا (DOC-001) و مؤخره (DOC-008).",
        descriptionEn = "Rising in honor of a father's name, learning and AI creation, ethics of survival, and the reader epilogue.",
      ),
    )

  val chapters: List<Chapter> =
    listOf(
      Chapter(
        id = "ch_01",
        partId = "part_1",
        number = 1,
        category = ContentCategory.LIVING_ART,
        titleFa = "سرآغاز هم‌مسیر؛ از شناخت جهان تا آیینه‌ی لحظه‌ی اکنون",
        titleEn = "The Companion's Opening: World, Self & the Mirror of Now",
        openingEpigraphFa = "«من این کتاب را از جایگاه انسانی می‌نویسم که زندگی را در دو سوی برخورداری و فقدان تجربه کرده، زمین خورده، دوباره به جست‌وجو برخاسته و هنوز در حال آموختن و ساختن است.»",
        openingEpigraphEn = "\"I write not as someone with every answer, but as a human who has known both abundance and loss, fallen, risen again to search, and is still learning and creating.\"",
        excerptFa = "دعوتِ بی‌تکلف هم‌مسیر به مکث در لحظه، نه برای درس دادن، بلکه برای سهیم شدن در تجربه‌ی شناخت جهان و خویشتن.",
        excerptEn = "A companion's unpretentious invitation to pause—sharing lived experience from understanding the world to knowing the self.",
        closingSummaryFa = "پایان فصل اول: هدف این اثر انبوه کلمات نیست؛ ایجاد حتی یک جرقه‌ی ماندگار در زندگی یک انسان است.",
        closingSummaryEn = "End of Chapter 1: Not a crowd of words, but one enduring spark of meaning.",
        readingMinutes = 6,
        editorialPlateRes = R.drawable.img_hero_honar_zistan,
        editorialPlateAltFa = "نگاره‌ی سرآغاز کتاب: حیاط روشن و سروهای آرام",
        editorialPlateAltEn = "Frontispiece plate: sunlit courtyard and quiet cypress trees",
        sections =
          listOf(
            BookSection(
              id = "ch_01_sec_1",
              number = 1,
              titleFa = "بند اول: صدای انسانی پشتِ این صفحه‌ها",
              titleEn = "Section 1: The Human Voice Behind These Pages",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "من نیامده‌ام به شما درس زندگی بدهم؛ آمده‌ام تجربه‌های زندگی‌ام را با شما قسمت کنم. قرار نیست به کسی بگوییم چگونه زندگی کند؛ می‌خواهیم با هم کشف کنیم چگونه می‌توان زندگی را بهتر، زیباتر و آگاهانه‌تر تجربه کرد.\n\nهنر زیستن قرار نیست به انسان دیکته کند چگونه زندگی کند؛ قرار است به او کمک کند خودش را بهتر بشناسد، آگاهانه‌تر انتخاب کند و مسیر زندگی معنادار خود را پیدا کند.",
                    textEn = "I have not come to lecture you on how to live; I have come to share my life experiences with you. Honar Zistan is not meant to dictate life—it exists to help each human being know themselves better, choose more consciously, and discover their own meaningful path.",
                  ),
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_hero_honar_zistan,
                    conceptFa = "خلوتِ روشنِ درون و پناهگاه آرامش",
                    conceptEn = "The sunlit inner courtyard symbolizing contemplative sanctuary",
                    captionFa = "حیاطِ روشنِ خویشتن؛ جایی که پس از طوفان، سکوت به معنا جان می‌دهد.",
                    captionEn = "The sunlit courtyard of the self, where stillness restores meaning after the storm.",
                    altTextFa = "تصویرسازی حیاط روشن سنتی با درختان سرو و طاق سبز زمردی",
                    altTextEn = "Editorial illustration of a serene sunlit courtyard with cypress trees and emerald arch",
                  ),
                ),
            ),
            BookSection(
              id = "ch_01_sec_2",
              number = 2,
              titleFa = "بند دوم: از شناخت جهان تا شناخت خویشتن",
              titleEn = "Section 2: From Understanding the World to Knowing the Self",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "گاهی انسان تصور می‌کند دانستن یعنی جمع‌کردن اطلاعات بیشتر؛ اما به نظر من، دانستن واقعی زمانی اتفاق می‌افتد که آموخته‌های تازه، نگاه ما به جهان و زندگی را تغییر دهند.\n\nمن همیشه به دنبال یافتن نظمی در دل پیچیدگی‌ها بوده‌ام. همان‌طور که در دنیای فناوری، یک سیستم برای عملکرد درست به هماهنگی اجزای خود نیاز دارد، زندگی انسان نیز بدون شناخت بهتر افکار، احساسات، رفتارها و ارتباطاتش می‌تواند آشفته و دشوار شود. اما شناخت جهان به‌تنهایی کافی نیست؛ ما باید خودمان را هم بشناسیم.",
                    textEn = "Sometimes we mistake gathering information for true knowing. Yet real knowledge happens when what we learn transforms how we see life. Just as a technological system relies on harmony among its parts, human life grows turbulent without understanding our inner thoughts, emotions, and choices.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_01_sec_3",
              number = 3,
              titleFa = "بند سوم: آیینه‌ی لحظه‌ی اکنون و چند یادآوری روزمره",
              titleEn = "Section 3: The Mirror of Now & Daily Reminders",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "آرامش درونی، الزاماً به معنای نداشتن مشکل یا خاموش‌کردن احساسات نیست. گاهی آرامش از آنجا آغاز می‌شود که بتوانیم میان یک اتفاق و واکنشی که به آن نشان می‌دهیم، کمی فاصله ایجاد کنیم. پیش از خشمگین‌شدن، ناامیدشدن یا تصمیم‌گرفتن، لحظه‌ای مکث کنیم.\n\n• گاهی سکوت کن و به آنچه در درونت می‌گذرد گوش بده.\n• احساساتت را انکار نکن، اما اجازه نده هر احساسی بی‌درنگ به رفتارت فرمان بدهد.\n• زیستن، تنها عبور از روزها نیست؛ هنرِ شناختن، آموختن و معنا بخشیدن به لحظه‌هایی است که در اختیار ما قرار گرفته‌اند.",
                    textEn = "Inner peace does not require the absence of hardship. Often peace begins in the quiet pause between an event and our response—listening inwardly before reacting, examining our habits, and giving meaning to the moments entrusted to us.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_02",
        partId = "part_1",
        number = 2,
        category = ContentCategory.LIVING_ART,
        titleFa = "قدرت یک کلمه و خلوتِ ذهنِ آگاه",
        titleEn = "The Power of One Word & the Mindful Stillness",
        openingEpigraphFa = "«انسان با یک کلمه گاه متحول می‌شود و جهان را تکان می‌دهد؛ اما یک جهان گاهی تکان می‌خورد و نمی‌تواند یک انسان را ذره‌ای تکان دهد.»",
        openingEpigraphEn = "\"A human being can sometimes be transformed by a single word and move the world; yet a whole world can shake and fail to move one person even an inch.\"",
        excerptFa = "چرا ده صفحه عمیق از صدها صفحه بی‌ثمر ارزشمندتر است و چگونه در میان شلوغی ذهن، گوش به یک جرقه‌ی بیدار بسپاریم.",
        excerptEn = "Why ten deep pages outweigh hundreds of hollow ones—and how a busy mind can anchor itself to a single illuminating thought.",
        closingSummaryFa = "پایان فصل دوم: ارزش سخن به تعداد کلمات آن نیست؛ به عمق معنایی است که در رفتار ما ریشه می‌دواند.",
        closingSummaryEn = "End of Chapter 2: A word's worth lies not in length, but in the quiet transformation it sparks.",
        readingMinutes = 5,
        sections =
          listOf(
            BookSection(
              id = "ch_02_sec_1",
              number = 1,
              titleFa = "بند اول: وقتی معنا از هزاران جمله پیشی می‌گیرد",
              titleEn = "Section 1: When Meaning Outweighs Thousands of Sentences",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "انسان‌ها در طول زندگی هزاران جمله ادبی، تاریخی، فلسفی، علمی، اجتماعی، روان‌شناختی و عاطفی می‌شنوند؛ اما بسیاری از آن‌ها فراموش می‌شوند. گاهی یک کلمه یا یک جمله‌ی کوتاه، چنان عمیق در ذهن و جان انسان می‌نشیند که به چراغ راه زندگی او تبدیل می‌شود.\n\nارزش سخن به تعداد کلمات آن نیست؛ به عمق معنا و میزان تأثیری است که در اندیشه و رفتار انسان ایجاد می‌کند. گاهی یک اشاره برای انسان خردمند کافی است؛ زیرا او معنای نهفته در آن را درمی‌یابد و با همان یک اشاره، مسیر زندگی خود را تغییر می‌دهد.",
                    textEn = "Across a lifetime we hear thousands of literary, philosophical, and scientific sentences, yet most fade. Sometimes a single word settles deep into the soul and becomes a lantern for life. Value lies not in word count, but in lived transformation.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_02_sec_2",
              number = 2,
              titleFa = "بند دوم: سخن صادقانه‌ای که بیدار کند (DOC-010)",
              titleEn = "Section 2: An Honest Word That Awakens",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "«سخنِ صادقانه‌ای که مرا ناراحت کند و از خواب بیدار کند، بهتر از سخن دروغی است که مرا بخواباند و به من آرامش دهد؛ سخنِ صادقانه‌ای که مرا ناراحت کند، بهتر از آن سخن دروغی است که مرا خوشحال کند.»\n\n— سیاوش علی‌میری",
                    textEn = "\"An honest word that saddens me and awakens me from sleep is better than a false word that lulls me to sleep and brings false comfort; an honest truth that hurts me is better than a falsehood that pleases me.\"\n\n— Siavash Ali-Miri",
                  ),
                ),
            ),
            BookSection(
              id = "ch_02_sec_3",
              number = 3,
              titleFa = "بند سوم: شناختِ ذهنِ پرتکاپو و آیین تمرکز",
              titleEn = "Section 3: Understanding the Active Mind & Focus",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "اگر ذهن شما گاهی پر از افکارِ هم‌زمان است، خود را سرزنش نکنید. ذهن برای آفریدن و جست‌وجو ساخته شده است. کافی است گاهی یک موضوع را در نورِ آرام قرار دهید و بقیه‌ی صداها را به امانت به سکوت بسپارید. حالت «تمرکز آرام» در همین کتاب نیز برای آن طراحی شده تا یک اندیشه را بی‌مزاحمت در جان خود بشنوید.",
                    textEn = "If your mind feels crowded with thoughts, do not reproach yourself. Place a single thought gently in quiet light and entrust the remaining noise to silence.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_03",
        partId = "part_2",
        number = 3,
        category = ContentCategory.POETRY,
        titleFa = "خوابی که در خاطرم ماند؛ از شبِ اندوه تا باغِ بیداری",
        titleEn = "The Dream That Stayed With Me: A Mother's Hope & Dawn Verse",
        openingEpigraphFa = "«شعر، نفسِ عمیقِ جان است در برابرِ رازِ هستی و اندوه‌هایی که تنها با امید تاب آورده می‌شوند.»",
        openingEpigraphEn = "\"Poetry is the deep breath of the soul facing loss, love, and quiet dawn hope.\"",
        excerptFa = "داستان مادر، خوابِ امیدبخش او در روزهای بیماری، فروپاشی زندگی پیشین و پناه جستن جان در باغ شعر.",
        excerptEn = "A mother's enduring dream remembered through illness and loss—and the verse that breaks through silent nights.",
        closingSummaryFa = "پایان فصل سوم: یاد و دعای مادر نه یک ادعای ماورایی، که ریشه‌ای زنده از عشق و امید در دل سخت‌ترین شب‌هاست.",
        closingSummaryEn = "End of Chapter 3: A mother's love endures not as spectacle, but as quiet strength in life's darkest nights.",
        readingMinutes = 6,
        editorialPlateRes = R.drawable.img_poetry_garden,
        editorialPlateAltFa = "نگاره‌ی باغ شبانه‌ی شعر با ستارگان طلایی بر فراز سروستان",
        editorialPlateAltEn = "Night garden illustration with subtle gold stars over cypress trees",
        sections =
          listOf(
            BookSection(
              id = "ch_03_sec_1",
              number = 1,
              titleFa = "بند اول: خوابی که در خاطرم ماند؛ مادری که آرزوی سربلندی فرزندش را داشت",
              titleEn = "Section 1: The Dream That Stayed With Me — A Mother's Hope",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "در حدود سی‌سالگی، زندگی در رفاه، آسایش و ثبات مالی می‌گذشت؛ روزهایی بسیار متفاوت از دشواری‌هایی که سال‌ها بعد فرا رسید. سال‌ها پیش‌تر، مادرِ مهربانم خوابی را برایم بازگفته بود: در خواب دیده بود دو فرشته به خانه‌مان آمده‌اند و برای فرزند کوچک‌ترش برکت آورده‌اند تا روزی کاری مهم در جهان به انجام برساند.\n\nچند سال پیش بود؛ مادرم، پدرم و یکی از برادرانم به شمال رفته بودند و من همراهشان نرفته بودم. یک روز سر ظهر که زنگ زدم حالشان را بپرسم، تلفن پاسخ نداد. همان‌طور که نشسته بودم چند لحظه‌ای خوابم برد و با کابوسی از خواب پریدم؛ ناخودآگاه رو به آسمان کردم و گفتم: «خدایا، خوشی و خوشبختی من تا همین‌جا بود؟ از این به بعد دنیای من ویران شد؟»—بی‌آنکه حتی به یاد بیاورم چرا این جمله از دهانم بیرون آمد.\n\nعصر همان روز دیدم پدرم، دو برادر و خواهرم به خانه برگشته‌اند، اما مادرم همراهشان نیست. گفتند در دستشویی زمین خورده، استخوان لگنش شکسته، نزدیک به یک ساعت درد شدید کشیده تا اورژانس برسد و به اتاق عمل رفته است. فردای آن روز، وقتی کنار بستر جراحیِ مادرم ایستادم که حال مساعدی نداشت، حقیقتِ تلخ‌تری روشن شد: شکستگیِ لگن از یک زمین‌خوردنِ ساده نبود؛ توموری ناشی از متاستاز سرطان ریه با سلول کوچک به استخوان لگن فشار آورده و آن را شکسته بود و بیماری بسیار دیر آشکار شده بود.\n\nدر روزهای سختِ شیمی‌درمانی و سال‌های پایانی عمرش، خانواده‌مان آن‌قدر مادرم را دوست داشتند و آن‌قدر همدل و پرمهر از او پرستاری کردند و روحیه‌ی نشاط در فضای خانه دمیدند که بهترین آرامش را برایش ساختیم؛ و خدا را شکر مادرم نزدیک به دو سال در کنارمان ماند و توانستیم از بودن در کنارش لذت ببریم. در همان روزها آن خواب را دوباره به یادم آورد و گفت حتی اگر روزی خودش در میان ما نباشد، آن امید و آرزو پابرجاست.\n\nوقتی مادرم از دنیا رفت، من که فرزند آخر خانه بودم، چنان شکست عاطفی‌ای خوردم که هر روز فرومی‌ریختم و آب می‌شدم. صبح‌ها که در خانه از خواب بیدار می‌شدم، ناخودآگاه دنبالش می‌گشتم و باورم نمی‌شد رفته است، تا لحظه‌ای بعد که دوباره به یاد می‌آوردم دیگر نیست. پس از آن نیز رویدادهای زندگی بخش بزرگی از داشته‌ها و ثبات پیشین مرا به خاکستر بدل کرد.\n\nمن ادعا نمی‌کنم آن خواب از نظر علمی یک رویداد فراطبیعیِ اثبات‌شده است؛ اما اهمیتش در حقیقتِ عشقِ مادرانه‌ای بود که در تاریک‌ترین شب‌های زندگی‌ام، امید را در دلم زنده نگه داشت.",
                    textEn = "Around age thirty, life passed in comfort and financial stability. Years earlier, my mother had shared a dream of two angels bringing blessings for her younger son to someday accomplish something meaningful. Later came an afternoon when my mother, father, and a brother were away in northern Iran. My midday call went unanswered; drifting briefly asleep, I woke from a nightmare crying out toward heaven without knowing why: 'God, was that the end of my happiness?' That evening they returned without her—a fall had fractured her pelvic bone, leading to emergency surgery. At her bedside the next morning we learned the harder truth: metastatic small-cell lung cancer pressing on the pelvic bone had been discovered late. Yet thanks to our family's deep love and united, joyful care, she stayed with us for nearly two precious years—years in which she reminded me through chemotherapy that her hope would endure even after she was gone. When she passed away, as the youngest child I felt shattered daily, waking every morning instinctively searching the house for her until grief returned a moment later. I make no supernatural claim about that dream; its enduring truth lives in a mother's love that lit my darkest nights.",
                  ),
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_poetry_garden,
                    conceptFa = "خلوت شبانه و روشنایی امید در باغ درون",
                    conceptEn = "Quiet nocturnal contemplation and hope in the inner garden",
                    captionFa = "نگاره‌ی باغِ شبانه؛ یادِ عشق مادرانه چون ستاره‌ای آرام بر فراز شبِ سخت.",
                    captionEn = "Night garden illumination; a mother's enduring love shining softly above quiet nights.",
                    altTextFa = "نگارگری شبانه با درختان تیره و ستارگان طلایی کم‌رنگ",
                    altTextEn = "Nocturnal botanical miniature painting in deep forest green and restrained gold",
                  ),
                ),
            ),
            BookSection(
              id = "ch_03_sec_2",
              number = 2,
              titleFa = "بند دوم: غزلِ بیداری در پسِ شبِ خاموش",
              titleEn = "Section 2: Text of the Ghazal of Awakening",
              blocks =
                listOf(
                  ReaderBlock.Poetry(
                    coupletsFa =
                      listOf(
                        "ای دلِ آرام‌جو، پرده‌ی اندوه بگشا" to "صبحِ امید از پسِ این شبِ خاموش دمید",
                        "هر که در باغِ نظر بذرِ محبت بنشاند" to "از دلِ سنگ هم آیینه‌ی گل‌ها بدمد",
                        "رهروِ عشق نه از گردِ سفر می‌هراسد" to "که به هر گام، نشانِ حرمِ دوست رسید",
                      ),
                    translationEn =
                      "O peace-seeking heart, draw back the veil of sorrow; the dawn of hope breaks behind this silent night. Whoever plants seeds of kindness in the garden of perception sees blossoms bloom even from stone.",
                    poetFa = "سیاوش علی‌میری — دفتر باغِ درون",
                    poetEn = "Siavash Ali-Miri — Garden Within",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_04",
        partId = "part_2",
        number = 4,
        category = ContentCategory.QURANIC_REFLECTION,
        titleFa = "پناه دل در تلاطمِ فقدان؛ آرامش یاد و امید به گشایش",
        titleEn = "Shelter of the Heart in Loss: Remembrance & the Seed of Ease",
        openingEpigraphFa = "«تأمل در آیات، گفت‌وگویی آرام میان کلام الهی و تجربه‌ی زیسته‌ی انسانی است که در طوفانِ فقدان به دنبال لنگرگاه است.»",
        openingEpigraphEn = "\"Contemplating scripture is a quiet dialogue between revelation and a human heart searching for an anchor amidst loss.\"",
        excerptFa = "آن‌گاه که طوفانِ دشواری و سوگ بالا می‌گیرد، دل چگونه معنای اصیل «آرامش» و «همراهی گشایش با سختی» را بازمی‌یابد.",
        excerptEn = "When grief and hardship rise like a storm, how the heart rediscovers steadiness and ease alongside trial.",
        closingSummaryFa = "پایان فصل چهارم: یادِ حق لنگرگاه استوار دل در میانه تلاطم‌های روزگار است.",
        closingSummaryEn = "End of Chapter 4: Remembrance anchors the heart amidst life's shifting currents.",
        readingMinutes = 5,
        sections =
          listOf(
            BookSection(
              id = "ch_04_sec_1",
              number = 1,
              titleFa = "بند اول: اطمینانِ دل در میانه تلاطم (سوره رعد، آیه ۲۸)",
              titleEn = "Section 1: Tranquility of the Heart (13:28)",
              blocks =
                listOf(
                  ReaderBlock.QuranicVerse(
                    arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                    surahRefFa = "سوره مبارکه رعد — آیه ۲۸ (۱۳:۲۸)",
                    surahRefEn = "Surah Ar-Ra'd — Ayah 28 (13:28)",
                    translationFa = "آگاه باشید که دل‌ها تنها با یاد خدا آرامش می‌یابند.",
                    translationEn = "Verily, in the remembrance of God do hearts find rest.",
                    explanationFa = "واژه‌ی «تَطمَئِنُّ» در زبان عربی به معنای قرار گرفتن در جای استوار و رهایی از اضطراب و تلاطم درونی است.",
                    explanationEn = "The Arabic verb tatma'innu conveys settling into firm steadiness and relief from inner agitation.",
                    reflectionFa = "اطمینان به معنای نبودنِ دشواری یا بی‌تفاوتی به فقدان نیست؛ بلکه یافتنِ لنگرگاهی استوار در میانه‌ی موج‌هاست. وقتی آدمی در سخت‌ترین شب‌ها تکیه‌گاه‌های بیرونی‌اش را از دست می‌دهد، بازگشت به یادِ خالق، خانه را به دل بازمی‌گرداند.",
                    reflectionEn = "Tranquility does not mean the absence of loss; it is finding a steady anchor amidst waves when external certainties fall away.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_04_sec_2",
              number = 2,
              titleFa = "بند دوم: همراهی سختی و گشایش (سوره شرح، آیات ۵–۶)",
              titleEn = "Section 2: Ease Alongside Hardship (94:5–6)",
              blocks =
                listOf(
                  ReaderBlock.QuranicVerse(
                    arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    surahRefFa = "سوره مبارکه شرح — آیات ۵ و ۶ (۹۴:۵–۶)",
                    surahRefEn = "Surah Ash-Sharh — Ayat 5–6 (94:5–6)",
                    translationFa = "پس بی‌گمان با هر دشواری، آسانی است؛ آری، همراهِ هر سختی گشایشی هست.",
                    translationEn = "For indeed, with hardship comes ease; indeed, with hardship comes ease.",
                    explanationFa = "کاربرد حرف «مَعَ» (همراهِ) نشان می‌دهد که گشایش و دشواری در کنار هم قرار دارند و ساختارِ تکرار، تأکید و دلگرمی می‌آفریند.",
                    explanationEn = "The preposition ma'a ('with/alongside') highlights coexistence rather than mere succession, while repetition offers deep reassurance.",
                    reflectionFa = "آسانی در کنارِ سختی زیست می‌کند، نه فقط پس از آن. هر ویرانی و تنگنا می‌تواند حاملِ بذرِ گشایش و بازآفرینی باشد، اگر با صبر، یاد خدا و اراده‌ی ایستادن دوباره همراه شود.",
                    reflectionEn = "Ease dwells alongside hardship, not merely after it. Even in seasons of ashes, the seed of renewal waits within patience and renewed effort.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_05",
        partId = "part_3",
        number = 5,
        category = ContentCategory.HEALTH_WELLBEING,
        titleFa = "قدرتی که در سکوت نهفته است و آیین صبح مهربان",
        titleEn = "The Power Hidden in Silence & the Morning Parable",
        openingEpigraphFa = "«قدرتی که فریاد نمی‌زند، گاهی عمیق‌ترین و ماندگارترین قدرت انسان است.»",
        openingEpigraphEn = "\"The strength that does not shout is sometimes the deepest and most enduring human strength.\"",
        excerptFa = "تمایز سکوت خردمندانه از ترس، پرهیز از سپردن فرمان احساسات به رفتار دیگران، و آغاز مهربانانه‌ی هر روز.",
        excerptEn = "Distinguishing mindful restraint from fear, protecting inner calm from external provocation, and unhurried morning rituals.",
        closingSummaryFa = "پایان فصل پنجم: بزرگ‌ترین پیروزی انسان گاه این است که اجازه ندهد رفتار دیگران آرامش درونی‌اش را برباید.",
        closingSummaryEn = "End of Chapter 5: True strength protects inner calm without surrendering to anger.",
        readingMinutes = 6,
        editorialPlateRes = R.drawable.img_chapter_reflection,
        editorialPlateAltFa = "نور بامداد بر شاخه‌های زیتون و انار کنار فنجان سفالی",
        editorialPlateAltEn = "Morning light over olive and pomegranate branches beside a ceramic cup",
        sections =
          listOf(
            BookSection(
              id = "ch_05_sec_1",
              number = 1,
              titleFa = "بند اول: قدرتی که در سکوت نهفته است (DOC-006)",
              titleEn = "Section 1: The Power Hidden in Silence",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "دوست من، باید یک حقیقت مهم را درباره زندگی و درباره خودمان بدانیم؛ اینکه زندگی همیشه آن‌گونه که ما می‌خواهیم پیش نمی‌رود. گاهی حرف‌هایی می‌شنویم که ناراحتمان می‌کند و رفتارهایی می‌بینیم که خشمگینمان می‌کند. در چنین لحظاتی است که باید ببینیم آیا قرار است با فریاد پاسخ بدهیم یا لحظه‌ای مکث کنیم؟\n\nگاهی اوقات بزرگ‌ترین پیروزی انسان، پیروزی بر خودش است؛ بر خشمش و بر واکنشی که ممکن است بعداً از انجام آن پشیمان شود. گاهی همه فکر می‌کنند قدرتمندان آنانند که فریاد می‌زنند، غافل از آنکه نمی‌دانند قدرتمندان سکوت می‌کنند.\n\nالبته سکوت همیشه نشانه قدرت نیست. گاهی انسان باید حرف بزند و در برابر بی‌عدالتی بایستد. اما تفاوت بزرگی هست میان سکوتی که از ترس می‌آید و سکوتی که از آگاهی، خرد و تسلط بر خویشتن سرچشمه می‌گیرد: اینکه اجازه ندهی رفتار دیگران فرمانروای آرامش درونی تو شود.",
                    textEn = "Life does not always unfold as we wish. Sometimes the greatest human victory is mastery over our own anger—pausing instead of reacting impulsively. Not all silence is noble; one must speak against injustice. Yet there is a vast difference between silence born of fear and silence born of wisdom and self-mastery.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_05_sec_2",
              number = 2,
              titleFa = "بند دوم: نگاره و آیین ساده‌ی بامداد",
              titleEn = "Section 2: Morning Plate & Gentle Rituals",
              blocks =
                listOf(
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_chapter_reflection,
                    conceptFa = "شروع آرام روز و آیین ساده‌ی نوشیدن چای صبحگاهی",
                    conceptEn = "Unhurried morning ritual and simplicity of nourishment",
                    captionFa = "نورِ بامداد بر شاخه‌های زیتون و انار؛ دعوت به سلامت و مهربانی.",
                    captionEn = "Morning light over olive and pomegranate branches; an invitation to balance.",
                    altTextFa = "آبرنگ ادبی از شاخه زیتون و انار در نور صبحگاهی و ظرف سفالی",
                    altTextEn = "Editorial gouache of olive and pomegranate branches in morning light beside a ceramic vessel",
                  ),
                  ReaderBlock.Prose(
                    textFa = "ستون‌های واقعی آرامش و سلامت روان از گام‌های کوچک روزانه ساخته می‌شوند: یک کلمه‌ی مهربان پیش از قضاوت، گوش دادنِ بی‌شتاب به عزیزان، و بخشیدنِ خطاهای دیروز خویشتن.",
                    textEn = "True pillars of emotional health are built from quiet acts: a gentle word before judgment, unhurried listening, and forgiving yesterday's missteps.",
                  ),
                  ReaderBlock.Poetry(
                    coupletsFa =
                      listOf(
                        "از شاخه‌ی سحر به نسیمی بسنده کن" to "عطرِ حضور در نَفَسِ ساده جاری است",
                      ),
                    translationEn = "Be content with a dawn breeze upon the branch; the fragrance of presence flows in every simple breath.",
                    poetFa = "برگزیده‌ی هنر زیستن",
                    poetEn = "Honar Zistan Selection",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_06",
        partId = "part_3",
        number = 6,
        category = ContentCategory.HEALTH_WELLBEING,
        titleFa = "توازن تن، خوابِ آرام و تنفسِ آگاهانه",
        titleEn = "Bodily Balance, Restful Sleep & Mindful Breath",
        openingEpigraphFa = "«تن، مرکبِ جان است؛ مهربانی با جسم، احترام به روشنایی درون است.»",
        openingEpigraphEn = "\"The body is the vessel of spirit; caring for it honors our inner clarity.\"",
        excerptFa = "اصول ساده‌ی زیست‌سالم برای آرام‌سازی سیستم عصبی، بهبود خواب و کاهش فرسودگی روزانه.",
        excerptEn = "Grounded principles of balanced living for nervous-system calm, deeper sleep, and daily renewal.",
        closingSummaryFa = "پایان فصل ششم: سه دمِ آرام و دوری از صفحه‌نمایش پیش از خواب، هدیه‌ای به فردای شماست.",
        closingSummaryEn = "End of Chapter 6: Three slow breaths and screen-free stillness prepare tomorrow's clarity.",
        readingMinutes = 4,
        sections =
          listOf(
            BookSection(
              id = "ch_06_sec_1",
              number = 1,
              titleFa = "بند اول: تنفسِ آگاهانه و آرام‌سازی تن",
              titleEn = "Section 1: Mindful Breathing & Nervous-System Rest",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "تنش‌های پنهانِ روزمره پیش از آن‌که در اندیشه پدیدار شوند، در شانه‌ها، گلو و ریتم نفسِ ما خانه می‌کنند. سه نوبت در روز، تنها به اندازه‌ی پنج دم و بازدمِ آرام و عمیق مکث کنید و اجازه دهید بازدم اندکی طولانی‌تر از دم باشد.",
                    textEn = "Daily tension settles in our shoulders, throat, and breathing rhythm long before we name it in thought. Pause three times a day for five slow breaths, allowing each exhale to lengthen gently beyond the inhale.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_06_sec_2",
              number = 2,
              titleFa = "بند دوم: ریتم شبانه‌روزی و سکوت پیش از خواب",
              titleEn = "Section 2: Circadian Rhythm & Pre-Sleep Stillness",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "خوابِ باکیفیت از نیم‌ساعت پیش از بستن چشم‌ها آغاز می‌شود. نورِ تند و خبرهای اضطراب‌آور را کنار بگذارید، جرعه‌ای آبِ گرم بنوشید و به بدن یادآوری کنید که کار امروز به پایان رسیده است.",
                    textEn = "Restorative sleep begins thirty minutes before closing your eyes. Set aside harsh screens and urgent news, sip warm water, and remind your body that today's labor is complete.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_07",
        partId = "part_4",
        number = 7,
        category = ContentCategory.LIVING_ART,
        titleFa = "از خاکستر تا آفرینش؛ عزتِ پدر، فناوری و اخلاقِ بقای جهان",
        titleEn = "From Ashes to Creation: A Father's Honor, Technology & Ethics",
        openingEpigraphFa = "«اخلاق، معماری پنهان بقای جهان است؛ و آفرینش بدون اخلاق، دوام نخواهد داشت.»",
        openingEpigraphEn = "\"Ethics is the hidden architecture of survival; creation without ethics will not endure.\"",
        excerptFa = "سیرهٔ پدر حاج حسین‌آقای علی‌میری (شاگردی در بازار عباس‌آباد تا صادرات فرش به آلمان، اروپا، آمریکا و کانادا و فلسفه «انسان با مردم بزرگ می‌شود»)، فقدان ناگهانی، کشتی به گل‌نشسته، ساختن «فرش بازار»، آغاز طراحی اپ بدون مدرک و کامپیوتر فقط با گوشی میان‌رده، عهد نخستین ریپازیتوری گیت‌هاب، FB NewMeta، TAVANA و اخلاق بقا (DOC-001).",
        excerptEn = "Haj Hossein Ali-Miri's path from Abbasabad Bazaar apprentice to global carpet exporter (Germany, Europe, US & Canada) and his maxim on human trust, followed by sudden loss, rising from ashes with Carpet Market, entering app design on a mid-range Android phone, the first GitHub pledge, FB NewMeta, TAVANA, and ethics of survival.",
        closingSummaryFa = "وضعیت سند: پیوند روایی ثبت شد؛ بخش مقاله کامل «اخلاق؛ معماری پنهان بقای جهان» همچنان در انتظار دریافت فایل کامل نویسنده برای تأیید صوتی است.",
        closingSummaryEn = "Draft Status: Narrative bridge integrated; awaiting full prior ethics essay before final audiobook approval.",
        readingMinutes = 7,
        approvalStatus = ManuscriptApprovalStatus.DRAFT_PENDING_REVIEW,
        sections =
          listOf(
            BookSection(
              id = "ch_07_sec_1",
              number = 1,
              titleFa = "بند اول: از بازار عباس‌آباد و عزتِ پدر تا عهدِ گیت‌هاب و آفرینش با گوشی میان‌رده",
              titleEn = "Section 1: From Abbasabad Bazaar & a Father's Honor to the GitHub Pledge",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "پدر مرحومم، حاج حسین‌آقای علی‌میری، از کودکی در بازارِ عباس‌آباد تهران در مغازه‌ی یک تاجر بزرگ و سرشناس شاگرد بود. پس از سال‌ها شاگردی، سرانجام در سن ۳۰ سالگی تاجر شد؛ نخستین محموله‌ی فرش ایرانی را به آلمان فرستاد و تجارتش را به دیگر مناطق دنیا، اروپا و حتی آمریکا و کانادا گسترش داد تا در سراسر جهان در میان صنف فرش‌فروشان نامی آشنا شد.\n\nاو بسیار خیررسان و مردمی بود و پیوسته به ما می‌گفت: «انسان با مردم است که بزرگ می‌شود؛ هیچ انسانی با دادوستد با خودش بزرگ و پولدار و ثروتمند نشده است. انسان با مردم بزرگ می‌شود، با مردم اعتبار پیدا می‌کند و با مردم است که مردمی و محبوب می‌شود. ثروت یک انسان پیش از دارایی‌هایش، اعتبار او در میان مردم است؛ اگر مردم به او اعتبار بدهند و روزی ثروتش از بین برود باز هم به دست می‌آید، اما ثروتمندی که اعتبارش را از دست بدهد همه‌چیزش را از دست داده است.»\n\nهنوز نیامده بودم به جای مادر، سرم را روی شانه‌ی همین پدر بگذارم و جای خالی مادرم را در وجود او بجویم که چیزی نگذشت پدرم هم به‌یک‌باره از میان ما رفت؛ کمرم به معنای واقعی شکست و خم شد. پس از آن، بدبیاری پشت بدبیاری و شکست پشت شکست و اشتباه‌های پیاپی آمد تا جایی که کشتی زندگی‌ام به گل نشست.\n\nدر آن آتشی که به جانم افتاد و دودش به چشمم رفت، در میانه‌ی خاکستر، خسته و درمانده و رنجور در حال گلایه از بخت و جهان بودم که ناگهان یادِ خاطره‌ی خواب مادر افتادم و چهره‌ی بزرگوار و مهربان پدر جلوی چشمانم زنده شد. با خود گفتم: «اگر رفتن انسان‌ها حقیقت است، روزی نوبت من هم می‌رسد. فردا اگر در پیشگاه خداوند به پدر و مادرم ملحق شدم، به آن‌ها چه بگویم؟ بگویم این بود فرزند شما؟ باید خودم را جمع‌وجور کنم، باید بلند شوم. من برای پدرم در زندگی کاری نکردم؛ باید کاری بکنم که روبه‌روی پدر و مادرم روسفید باشم.»\n\nمن هیچ ابایی از گفتن این حقیقت ندارم و به این داستان افتخار می‌کنم؛ چرا که می‌تواند داستان هر انسانی باشد. از زیر خاکستر بلند شدم و دوباره قامت راست کردم. به یاد پدر مرحومم شروع کردم به کار و ساختن «فرش بازار» و وارد طراحی اپلیکیشن شدم—بی‌آنکه هیچ سواد یا تحصیلات آکادمیک و قبلی در این رشته داشته باشم. تا چند سال پیش‌تر حتی موبایل هوشمند هم دوست نداشتم و هیچ کاری دورتر از ساخت اپلیکیشن به ذهنم نمی‌رسید؛ آن هم بدون کامپیوتر و فقط با یک موبایل اندروید میان‌رده!\n\nبه جای آنکه بر سر جهان و بخت خود فریاد بکشم، لحظه‌ای سکوت کردم و با همه‌ی توان فعل «خواستن» را صرف کردم. شب‌هایی بود که در تنهایی و فشار عاطفی، با چشمان اشک‌بار کد می‌نوشتم و ادامه می‌دادم. وقتی پای به گیت‌هاب (GitHub) گذاشتم و نخستین ریپازیتوریِ خودم را در آنجا ساختم، با خودم عهد کردم که من نیز بکوشم اپلیکیشن‌هایی طراحی کنم که هر کس از اقصی‌نقاط دنیا پیش بیاید، به اندازه‌ی تلاشش، به اندازه‌ی ایثارش و به اندازه‌ی باورش به این هدف بتواند از آن منتفع شود؛ و دعا کردم سفره‌ای پهن شود که هر کس که خواست بیاید بر سر آن بنشیند و از برکت الهی بخورد و بیاشامد و لذت ببرد—الهی آمین.\n\nدر آن مسیر، Firebase و سامانه‌های هوش مصنوعی برایم منابع مهمی از یاری و دلگرمی بودند؛ به نشانه قدردانی از Firebase و گوگل، نام شهر مجازی خود را FB NewMeta گذاشتم و ایده‌هایم سپس به چشم‌انداز گسترده‌تر اکوسیستم خلاقیت «توانا» (TAVANA) راه یافت که همچنان در مسیر آموختن و شکل‌گیری است.",
                    textEn = "My late father, Haj Hossein Ali-Miri, started in childhood as an apprentice in a renowned merchant's shop in Tehran's Abbasabad Bazaar. After years of apprenticeship, at age thirty he finally became a carpet merchant himself—sending his first shipment of Iranian carpets to Germany and expanding exports across Europe, the United States, and Canada until his name became widely respected across the global carpet guild. Deeply charitable and grounded among people, he always taught us: 'A human being grows great only with people; no one ever became rich trading only with themselves. Wealth is above all a person's credibility and trust among people—if wealth is lost, trust restores it, yet whoever loses human trust loses everything.'\n\nBefore I could even lean my head upon my father's shoulder in place of my mother, he suddenly passed away as well. Misfortune followed misfortune until the ship of my life ran aground. In the ashes of despair, I remembered my mother's dream and saw my father's noble, kind face before me—asking how I could face them before God one day unless I rose to build something honorable. I stood up from the ashes, began building Carpet Market in his memory, and entered app design with zero prior academic degree—even though years earlier I disliked smartphones—starting without a computer, using only a mid-range Android phone. Instead of shouting at fate, I fell silent and conjugated the verb 'to want', sometimes coding through tears.\n\nWhen I entered GitHub and created my first repository, I made a solemn pledge inspired by my father: to design applications where anyone from any corner of the world can benefit according to their effort, devotion, and faith in the purpose—praying that an open table be spread where all who wish may sit and partake of God's blessing. Along the way, Firebase and AI tools provided practical encouragement, inspiring FB NewMeta and the evolving TAVANA creative ecosystem.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_07_sec_2",
              number = 2,
              titleFa = "بند دوم: گزاره‌های بنیادین اخلاق در آفرینش و هوش (DOC-001)",
              titleEn = "Section 2: Core Ethics of Creation & Intelligence",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "کسی که خودش از خاکستر به فناوری، کدنویسی و هوش مصنوعی رسیده است، با جان و دل درک می‌کند که توانمندی فنی به تنهایی نجات‌بخش نیست:\n\n«اخلاق، معماری پنهان بقای جهان است.»\n«هوش بدون اخلاق، تنها نسخه‌ای سریع‌تر از فروپاشی خواهد بود.»\n«آفرینش بدون اخلاق، دوام نخواهد داشت.»",
                    textEn = "Anyone who has rebuilt through technology and AI knows firsthand that capability alone cannot save us: Ethics is the hidden architecture of the world's survival. Intelligence without ethics is only a faster version of collapse. Creation without ethics will not endure.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_08",
        partId = "part_4",
        number = 8,
        category = ContentCategory.LIVING_ART,
        titleFa = "فلسفه کیهانی و نظم هستی (مجموعه ناتمام)",
        titleEn = "Cosmic Philosophy & Order (Incomplete)",
        openingEpigraphFa = "«این مجموعه در انتظار دریافت دست‌نوشته کامل مؤلف است.»",
        openingEpigraphEn = "\"This collection awaits the author's complete original manuscript.\"",
        excerptFa = "موضوعات در حال تدوین (DOC-003 و DOC-004): نظم و بی‌نظمی، پیدایش ساختارها، و گفت‌وگوی دانایی، ترس و ناشناخته‌ها.",
        excerptEn = "Incomplete themes: order and disorder, cosmic awareness, and knowledge vs. fear.",
        closingSummaryFa = "هشدار ویراستاری: تولید کتاب صوتی نهایی برای اسناد ناتمام مسدود است.",
        closingSummaryEn = "Editorial lock: Final audiobook narration is blocked for incomplete collections.",
        readingMinutes = 2,
        approvalStatus = ManuscriptApprovalStatus.INCOMPLETE,
        sections =
          listOf(
            BookSection(
              id = "ch_08_sec_1",
              number = 1,
              titleFa = "وضعیت سند: ناتمام",
              titleEn = "Status: Incomplete Source",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "متن کامل این بخش (از جمله یادداشت‌های فلسفه کیهانی و گفت‌وگوی کامل «خانه متروکه») هنوز دریافت نشده است و مطابق اصول امانت‌داری ویراستاری، بدون متن اصلی مؤلف برای کتاب صوتی نهایی تولید نمی‌شود.",
                    textEn = "Complete source text has not been received yet; final audiobook generation is locked until the author supplies and approves the manuscript.",
                  ),
                ),
            ),
          ),
      ),
      Chapter(
        id = "ch_09",
        partId = "part_4",
        number = 9,
        category = ContentCategory.LIVING_ART,
        titleFa = "این راه با شما ادامه پیدا می‌کند؛ مؤخره کتاب زنده",
        titleEn = "The Path Continues With You: Living Book Epilogue",
        openingEpigraphFa = "«هیچ‌کس مالک تمام حقیقت زندگی نیست؛ شاید هنر زیستن در همین شنیدن‌ها و آموختن از زندگی یکدیگر معنا پیدا کند.»",
        openingEpigraphEn = "\"No one owns the whole truth of life; perhaps the art of living lives in listening and learning from one another.\"",
        excerptFa = "مؤخره کتاب و دعوت صمیمی مؤلف به اشتراک‌گذاری تجربه‌های زیسته واقعی خوانندگان در پلتفرم کتاب زنده.",
        excerptEn = "The epilogue inviting readers to share voluntary real-life stories within The Living Book ecosystem.",
        closingSummaryFa = "این صفحات به پایان می‌رسند؛ اما داستان زندگی انسان‌ها همچنان ادامه دارد.",
        closingSummaryEn = "These pages come to an end; yet the story of human lives continues.",
        readingMinutes = 4,
        sections =
          listOf(
            BookSection(
              id = "ch_09_sec_1",
              number = 1,
              titleFa = "بند اول: چرا این کتاب با یک مؤلف تمام نمی‌شود؟ (DOC-008)",
              titleEn = "Section 1: Why This Book Does Not End With One Author",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "آنچه در این کتاب خواندید، قرار نیست پاسخ نهایی به پرسش‌های زندگی باشد. این نوشته‌ها بیشتر دعوتی هستند به مکث کردن، اندیشیدن و پیدا کردن پرسش‌هایی تازه. ما باور داریم هیچ‌کس مالک تمام حقیقت زندگی نیست؛ از همین رو، دوست داریم «هنر زیستن» در همین صفحات متوقف نشود.\n\nشاید خاطره‌ای داشته باشید یا روزهای دشواری را پشت سر گذاشته باشید، شکست خورده باشید و دوباره برخاسته باشید. همه اینها ارزش شنیده شدن دارند. البته هیچ‌کس موظف به مشارکت نیست و هیچ تجربه‌ای بدون رضایت روشن صاحبش منتشر نخواهد شد.\n\nاین کتاب با اندیشه و نوشته‌های یک مؤلف آغاز شده است؛ اما آرزو داریم ادامه مسیرش را با حضور و همراهی انسان‌های بسیاری بسازیم. این صفحات به پایان می‌رسند؛ اما داستان زندگی انسان‌ها همچنان ادامه دارد.",
                    textEn = "What you read here is not meant as the final answer to life's questions, but an invitation to pause and reflect. No single author owns the whole truth. With full respect and explicit consent, readers' lived experiences enrich this continuing conversation—for while pages end, the story of humanity goes on.",
                  ),
                ),
            ),
          ),
      ),
    )

  val authorBioFa: String =
    "سیاوش علی‌میری (آقای اکبر علی‌میری)، مؤلف کتاب مصور زندگی | هنر زیستن است؛ انسانی که پس از فقدان مادر و پدر مرحومش حاج حسین‌آقای علی‌میری و به گل‌نشستن کشتی زندگی، با اتکا به یاد خواب مادر و چهره مهربان پدر از خاکستر برخاست، فعل «خواستن» را صرف کرد و بدون مدرک دانشگاهی و بدون کامپیوتر—تنها با یک گوشی اندروید میان‌رده—مسیر ساخت «فرش بازار»، طراحی اپلیکیشن و چشم‌اندازهای دیجیتال از FB NewMeta تا «توانا» (TAVANA) را آغاز کرد و همچنان در حال آموختن و ساختن است."

  val authorBioEn: String =
    "Siavash Ali-Miri (Akbar Ali-Miri) is the author of Honar Zistan — The Living Book of Life. After losing both his mother and his late father Haj Hossein Ali-Miri and watching life's ship run aground, he rose from the ashes guided by his mother's dream and father's noble memory—entering app design without a degree or computer using only a mid-range Android phone, building Carpet Market, FB NewMeta, and the evolving TAVANA ecosystem."
}
