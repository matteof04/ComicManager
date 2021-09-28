# ComicManager
**ComicManager** is an app to convert and optimize comic image file/folders for eReaders.

ComicManager is a Kotlin rewrite of [_KCC_](https://github.com/ciromattia/kcc).

Currently, ComicManager works only in the command line, but a UI implementation is planned.

ComicManager is released under the GNU GPLv3 license. For more information see the LICENSE.txt file or the [GNU Project license page](https://www.gnu.org/licenses/).

### Supported input format
- JPEG/JPG
- PNG
- GIF
- TIFF
- WebP

### Supported output format
- CBZ
- EPUB 3
- KEPUB
- MOBI ([Require KindleGen](https://web.archive.org/web/20190817070956/https://www.amazon.com/gp/feature.html?docId=1000765211)) (Not tested)

### Supported devices
- Kobo Forma
- Kobo Clara HD
- Kobo Libra H20
- Kobo Mini/Touch (KINDLE_MT)
- Kobo Glo
- Kobo Glo HD
- Kobo Aura
- Kobo Aura HD
- Kobo Aura H2O
- Kobo Aura ONE
- Kindle 1
- Kindle 2
- Kindle Keyboard/Touch (KINDLE_KT)
- Kindle DX/DXG (KINDLE_DXG)
- Kindle Paperwhite 1/2
- Kindle Paperwhite 3/4
- Kindle Voyage
- Kindle Oasis 1
- Kindle Oasis 2/3
###### NOTE: Only the Kobo Forma has been tested

## Usage
Currently, ComicManager is available only as a CLI software. A GUI is coming soon.
```
Usage: ComicManager options_list
Options: 
    --device, -d -> The device you want to CM prepare image for ~ Note: if custom height and width are set this will be ignored { Value should be one of [kobo_forma, kobo_clara_hd, kobo_libra_h20, kobo_mt, kobo_glo, kobo_glo_hd, kobo_aura, kobo_aura_hd, kobo_aura_h2o, kobo_aura_one, kindle_1, kindle_2, kindle_kt, kindle_dxg, kindle_paperwhite_1_2, kindle_paperwhite_3_4, kindle_voyage, kindle_oasis_1, kindle_oasis_2_3] }
    --height [0] -> The height of your device in portrait mode (use this if your device is not a supported device) { Int }
    --width [0] -> The width of your device in portrait mode (use this if your device is not a supported device) { Int }
    --format, -f -> The format you want (if none is provided, CM choose the better format based on your device) { Value should be one of [epub, cbz, kepub, mobi] }
    --page-progression-direction, -ppd [ltr] -> Select page progression direction { Value should be one of [ltr, rtl] }
    --background-color, -bg [NONE] -> Background color { Value should be one of [white, black, auto, none] }
    --resize-mode, -rm [UPSCALE] -> Choose resize mode { Value should be one of [stretch, upscale, nothing] }
    --split-mode, -sm [SPLIT] -> Choose split mode { Value should be one of [rotate, split] }
    --contrast, -c [1.0] -> Manually adjust the image contrast { Double }
    --no-autocontrast, -noac [false] -> Disable autocontrast ~ Note: if autocontrast is enabled contrast passed value is ignored 
    --author, -a [ComicManager] -> Manually set the author { String }
    --split-volumes, -sv -> Split the input directory in multiple files containing the specified number of chapters { Int }
    --output, -o -> Output filename without extension { String }
    --input, -i -> Input dir (always required) { String }
    --license, -l [false] -> Show the license 
    --sync, -s [false] -> Elaborate the page synchronously 
    --help, -h -> Usage info 
```

### Known Issue
If the software throws an OutOfMemory exception, try to run in with the Xmx JVM argument with more than 2GB of RAM. If the problem persist, try to run the software with  ``` --sync ```  argument.
