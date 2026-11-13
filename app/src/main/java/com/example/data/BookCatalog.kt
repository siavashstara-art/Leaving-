package com.example.data

import com.example.R
import com.example.model.BookPart
import com.example.model.BookSection
import com.example.model.Chapter
import com.example.model.ContentCategory
import com.example.model.ReaderBlock

object BookCatalog {
  const val BOOK_TITLE_FA = "هنر زیستن"
  const val BOOK_SUBTITLE_FA = "کتاب مصور زندگی"
  const val BOOK_TITLE_EN = "Honar Zistan"
  const val BOOK_SUBTITLE_EN = "The Illustrated Book of Living"
  const val AUTHOR_FA = "سیاوش علی‌میری"
  const val AUTHOR_EN = "Siavash Ali-Miri"

  val parts: List<BookPart> =
    listOf(
      BookPart(
        id = "part_1",
        number = 1,
        titleFa = "دفتر اول: مبانی حضور و هنر زیستن",
        titleEn = "Part I: Foundations of Presence & the Art of Living",
        descriptionFa = "درآمدی تأملی بر مکث، آگاهی در لحظه‌ی اکنون و آشتی با جریان آرام زندگی.",
        descriptionEn = "A reflective introduction to pausing, presence in the now, and peace with daily life.",
      ),
      BookPart(
        id = "part_2",
        number = 2,
        titleFa = "دفتر دوم: شعر، ادب و تأملات قرآنی",
        titleEn = "Part II: Poetry, Literature & Quranic Reflections",
        descriptionFa = "پیوند لطافت شعر فارسی و نور وحی در آرام‌سازی دل و اندیشه.",
        descriptionEn = "Where Persian poetic tradition meets sacred verses for inner steadiness.",
      ),
      BookPart(
        id = "part_3",
        number = 3,
        titleFa = "دفتر سوم: سلامت، تندرستی و زیستِ متعادل",
        titleEn = "Part III: Health, Wellbeing & Balanced Living",
        descriptionFa = "عادت‌های ساده و سازنده‌ی روزانه برای تندرستی جسم، آرامش روان و کیفیت خواب.",
        descriptionEn = "Practical, contemplative rhythms for physical vitality, emotional calm, and restorative rest.",
      ),
    )

  val chapters: List<Chapter> =
    listOf(
      Chapter(
        id = "ch_01",
        partId = "part_1",
        number = 1,
        category = ContentCategory.LIVING_ART,
        titleFa = "مقدمه؛ در آیینه‌ی لحظه‌ی اکنون",
        titleEn = "Prologue: In the Mirror of Now",
        openingEpigraphFa = "«زیستنِ آگاهانه، هنرِ شنیدنِ سکوت میانِ شتاب‌هاست.»",
        openingEpigraphEn = "\"Conscious living is the art of hearing stillness between hastes.\"",
        excerptFa = "زندگی مجموعه‌ی ساعت‌هایی نیست که می‌گذرند، بلکه عمق نگاهی است که به همین دم هدیه می‌دهیم.",
        excerptEn = "Life is not the sum of passing hours, but the depth of attention we gift to this very breath.",
        closingSummaryFa = "پایان فصل اول: هر دم می‌تواند نقطه‌ی آغاز آشتی دوباره با خویشتن باشد.",
        closingSummaryEn = "End of Chapter 1: Each breath offers a quiet return to inner peace.",
        readingMinutes = 4,
        editorialPlateRes = R.drawable.img_hero_honar_zistan,
        editorialPlateAltFa = "نگاره‌ی سرآغاز کتاب: حیاط روشن و سروهای آرام",
        editorialPlateAltEn = "Frontispiece plate: sunlit courtyard and quiet cypress trees",
        sections =
          listOf(
            BookSection(
              id = "ch_01_sec_1",
              number = 1,
              titleFa = "بند اول: دعوت به مکث",
              titleEn = "Section 1: An Invitation to Pause",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "دوستِ همراه، به خانه‌ی آرامِ «هنر زیستن» خوش آمدید. این کتاب برای شتاب خواندن نوشته نشده است؛ برای مکث کردن، نفس کشیدن و شنیدن صدای لطیفی است که در میان شلوغی روزمره‌ها فراموش می‌کنیم.",
                    textEn = "Dear companion, welcome to the quiet sanctuary of Honar Zistan. This book is not meant to be rushed; it is an invitation to pause, breathe, and hear the gentle voice often lost in daily haste.",
                  ),
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_hero_honar_zistan,
                    conceptFa = "خلوتِ روشنِ درون و پناهگاه آرامش",
                    conceptEn = "The sunlit inner courtyard symbolizing contemplative sanctuary",
                    captionFa = "حیاطِ روشنِ خویشتن؛ جایی که سکوت به معنا جان می‌دهد.",
                    captionEn = "The sunlit courtyard of the self, where stillness gives life to meaning.",
                    altTextFa = "تصویرسازی حیاط روشن سنتی با درختان سرو و طاق سبز زمردی",
                    altTextEn = "Editorial illustration of a serene sunlit courtyard with cypress trees and emerald arch",
                  ),
                ),
            ),
            BookSection(
              id = "ch_01_sec_2",
              number = 2,
              titleFa = "بند دوم: در ستایش سادگی روزمره",
              titleEn = "Section 2: In Praise of Daily Simplicity",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "هنر زیستن یعنی بتوانیم در ساده‌ترین فنجان چای صبحگاهی، در نگاهِ مهربان یک رهگذر و در سایه‌ی آرام یک درخت سرو، نشانه‌ای از حضور و زیبایی ببینیم. هر فصلِ این کتاب مصور پنجره‌ای است رو به همین آشتی دوباره با زندگی.",
                    textEn = "The art of living means recognizing presence and beauty in a simple morning cup of tea, a kind glance, and the calm shade of a cypress tree. Each illustrated chapter opens a window toward making peace with life.",
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
        titleFa = "تمرینِ توجه و خلوتِ ذهن",
        titleEn = "Practicing Attention & Mental Stillness",
        openingEpigraphFa = "«ذهنِ آرام نه خالی از فکر، که رها از شتابِ داوری است.»",
        openingEpigraphEn = "\"A calm mind is not empty of thought, but free from hurried judgment.\"",
        excerptFa = "راهکارهایی برای ذهن‌های پرتکاپو و پرفکر تا در میانه‌ی هیاهو جزیره‌ای از آرامش بسازند.",
        excerptEn = "Gentle practices for busy minds to build an island of calm amidst distraction.",
        closingSummaryFa = "پایان فصل دوم: به جای سرزنشِ پراکندگی ذهن، یک اندیشه را در نورِ آرام قرار دهید.",
        closingSummaryEn = "End of Chapter 2: Replace self-criticism with gentle single-pointed attention.",
        readingMinutes = 5,
        sections =
          listOf(
            BookSection(
              id = "ch_02_sec_1",
              number = 1,
              titleFa = "بند اول: شناختِ ذهنِ پرتکاپو",
              titleEn = "Section 1: Understanding the Active Mind",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "اگر ذهن شما گاهی پر از افکارِ هم‌زمان است، خود را سرزنش نکنید. ذهن برای آفریدن و جست‌وجو ساخته شده است. کافی است گاهی یک موضوع را در نورِ آرام قرار دهید و بقیه‌ی صداها را به امانت به سکوت بسپارید.",
                    textEn = "If your mind sometimes feels crowded with simultaneous thoughts, do not reproach yourself. Simply place one thought into gentle light and entrust the remaining noise to silence for a moment.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_02_sec_2",
              number = 2,
              titleFa = "بند دوم: آیین تمرکز در مطالعه",
              titleEn = "Section 2: The Practice of Focused Reading",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "حالت «تمرکز آرام» در همین کتاب نیز با همین نیت طراحی شده است تا بتوانید در هر لحظه، تنها یک فصل و یک بند را بی‌مزاحمت بخوانید و یادداشتی کوتاه از تأمل خود بنویسید.",
                    textEn = "The Focus Mode in this companion was crafted with this very spirit—helping you read one serene passage at a time without visual clutter, and capture a brief personal reflection.",
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
        titleFa = "غزلِ بیداری و باغِ دل",
        titleEn = "Ghazal of Awakening & the Heart's Garden",
        openingEpigraphFa = "«شعر، نفسِ عمیقِ جان است در برابرِ رازِ هستی.»",
        openingEpigraphEn = "\"Poetry is the deep breath of the soul before the mystery of being.\"",
        excerptFa = "در خلوتِ شبانه‌ی جان، هر بیت چون چراغی است که راه را نه با هیاهو، که با نور ملایم نشان می‌دهد.",
        excerptEn = "In the quiet night of the soul, each couplet shines like a gentle lantern illuminating the path.",
        closingSummaryFa = "پایان فصل سوم: شعر وقتی به زیستن راه می‌یابد که در رفتار و نگاهِ ما طنین بیندازد.",
        closingSummaryEn = "End of Chapter 3: Poetry fulfills its purpose when it resonates in daily conduct.",
        readingMinutes = 4,
        editorialPlateRes = R.drawable.img_poetry_garden,
        editorialPlateAltFa = "نگاره‌ی باغ شبانه‌ی شعر با ستارگان طلایی بر فراز سروستان",
        editorialPlateAltEn = "Night garden illustration with subtle gold stars over cypress trees",
        sections =
          listOf(
            BookSection(
              id = "ch_03_sec_1",
              number = 1,
              titleFa = "بند اول: سخنِ آغازین درباره شعر",
              titleEn = "Section 1: On the Literary Role of Poetry",
              blocks =
                listOf(
                  ReaderBlock.Prose(
                    textFa = "شعر در فرهنگِ ما تنها آرایش واژگان نیست؛ نفسِ عمیقِ جان است در برابر راز هستی. این ابیات را آرام و با مکث بخوانید.",
                    textEn = "In Persian heritage, poetry is not mere ornament—it is the deep breath of the soul facing existence. Read these couplets slowly.",
                  ),
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_poetry_garden,
                    conceptFa = "خلوت شبانه و روشنایی امید در باغ درون",
                    conceptEn = "Quiet nocturnal contemplation and hope in the inner garden",
                    captionFa = "نگاره‌ی باغِ شبانه؛ درخشش آرام ستارگان بر فراز سروستان.",
                    captionEn = "Night garden illumination; quiet starlight above the cypress grove.",
                    altTextFa = "نگارگری شبانه با درختان تیره و ستارگان طلایی کم‌رنگ",
                    altTextEn = "Nocturnal botanical miniature painting in deep forest green and restrained gold",
                  ),
                ),
            ),
            BookSection(
              id = "ch_03_sec_2",
              number = 2,
              titleFa = "بند دوم: متنِ غزل بیداری",
              titleEn = "Section 2: Text of the Ghazal",
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
        titleFa = "آرامش یاد و پناهِ دل",
        titleEn = "Serenity of Remembrance",
        openingEpigraphFa = "«تأمل در آیات، گفت‌وگویی آرام میان کلام الهی و تجربه‌ی زیسته‌ی انسان است.»",
        openingEpigraphEn = "\"Contemplating scripture is a quiet dialogue between revelation and lived experience.\"",
        excerptFa = "آن‌گاه که طوفانِ فکرها آرام می‌گیرد، دل معنای اصیل «اطمینان» را در یادِ حق بازمی‌یابد.",
        excerptEn = "When the storm of thoughts subsides, the heart rediscovers true tranquility in remembrance.",
        closingSummaryFa = "پایان فصل چهارم: یادِ حق لنگرگاه استوار دل در میانه تلاطم‌های روزگار است.",
        closingSummaryEn = "End of Chapter 4: Remembrance anchors the heart amidst life's shifting currents.",
        readingMinutes = 5,
        sections =
          listOf(
            BookSection(
              id = "ch_04_sec_1",
              number = 1,
              titleFa = "بند اول: اطمینانِ دل (سوره رعد، آیه ۲۸)",
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
                    reflectionFa = "اطمینان به معنای نبودنِ دشواری نیست؛ بلکه یافتنِ لنگرگاهی استوار در میانِ موج‌هاست. وقتی ذهن خسته می‌شود، بازگشت به یادِ خالق، خانه را به دل بازمی‌گرداند.",
                    reflectionEn = "Tranquility does not mean the absence of hardship; it is finding a steady anchor amidst the waves. Returning to remembrance brings the heart back home.",
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
                    reflectionFa = "آسانی در کنارِ سختی زیست می‌کند، نه فقط پس از آن. هر تنگنا حاملِ بذرِ گشایشی است که با صبر و بینش شکوفا می‌شود.",
                    reflectionEn = "Ease dwells alongside hardship, not merely after it. Every trial carries the seed of opening when tended with patience and insight.",
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
        titleFa = "حکایتِ صبح و فنجانِ مهربانی",
        titleEn = "Parable of Morning & Kindness",
        openingEpigraphFa = "«تندرستی روان از عادت‌های آرامِ بامداد آغاز می‌شود.»",
        openingEpigraphEn = "\"Wellbeing of spirit begins with unhurried morning rituals.\"",
        excerptFa = "چگونه عادت‌های کوچک و مهربانانه، رنگِ کل روز و سلامت روابطِ ما را دگرگون می‌کنند.",
        excerptEn = "How small, gentle rituals transform the color of our entire day and relational health.",
        closingSummaryFa = "پایان فصل پنجم: گام‌های کوچکِ روزانه از تصمیم‌های پرهیاهو ماندگارترند.",
        closingSummaryEn = "End of Chapter 5: Small daily rituals endure far longer than loud resolutions.",
        readingMinutes = 4,
        editorialPlateRes = R.drawable.img_chapter_reflection,
        editorialPlateAltFa = "نور بامداد بر شاخه‌های زیتون و انار کنار فنجان سفالی",
        editorialPlateAltEn = "Morning light over olive and pomegranate branches beside a ceramic cup",
        sections =
          listOf(
            BookSection(
              id = "ch_05_sec_1",
              number = 1,
              titleFa = "بند اول: نگاره و تأمل بامداد",
              titleEn = "Section 1: Morning Plate & Reflection",
              blocks =
                listOf(
                  ReaderBlock.Illustration(
                    drawableRes = R.drawable.img_chapter_reflection,
                    conceptFa = "شروع آرام روز و آیین ساده‌ی نوشیدن چای صبحگاهی",
                    conceptEn = "Unhurried morning ritual and simplicity of nourishment",
                    captionFa = "نورِ بامداد بر شاخه‌های زیتون و انار؛ دعوت به سادگی.",
                    captionEn = "Morning light over olive and pomegranate branches; an invitation to simplicity.",
                    altTextFa = "آبرنگ ادبی از شاخه زیتون و انار در نور صبحگاهی و ظرف سفالی",
                    altTextEn = "Editorial gouache of olive and pomegranate branches in morning light beside a ceramic vessel",
                  ),
                  ReaderBlock.Prose(
                    textFa = "در شتابِ جهان مدرن، اغلب گمان می‌کنیم بزرگی در کارهای پرسر‌وصداست. حال آن‌که ستون‌های واقعی آرامش و سلامت روان از همین گام‌های کوچک ساخته می‌شوند: یک کلمه‌ی مهربان پیش از قضاوت، گوش دادنِ بی‌شتاب به عزیزان، و بخشیدنِ خطاهای دیروز خویشتن.",
                    textEn = "In modern haste, we assume greatness lives in loud achievements. Yet true pillars of peace and emotional health are built from quiet acts: a gentle word before judgment, unhurried listening, and forgiving yesterday's missteps.",
                  ),
                ),
            ),
            BookSection(
              id = "ch_05_sec_2",
              number = 2,
              titleFa = "بند دوم: تک‌بیتِ سحرگاه",
              titleEn = "Section 2: Dawn Couplet",
              blocks =
                listOf(
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
        readingMinutes = 5,
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
    )

  val authorBioFa: String =
    "سیاوش علی‌میری، نویسنده و پژوهشگر حوزه‌ی فرهنگ، معنا و هنر زیستن است. کتاب مصور «هنر زیستن | کتاب مصور زندگی» حاصل سال‌ها تأمل در ادبیات فارسی، حکمت معنوی و تجربه‌ی زیسته‌ی روزمره است تا پلی صمیمی میان زیبایی بصری، شعر و آرامش درونی بسازد."

  val authorBioEn: String =
    "Siavash Ali-Miri is an author and thinker exploring Persian literary wisdom, mindful living, and spiritual stillness. Honar Zistan (The Illustrated Book of Living) brings together prose, selective Nastaliq poetry, Quranic contemplation, and botanical illustration as a calm daily companion."
}
