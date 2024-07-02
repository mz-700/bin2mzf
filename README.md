### BIN2MZF

A Java program to convert Z80 raw binary files to Sharp MZ MZF format.

__Parameters__

|Parameter|Required|Description|Default|
|:--------|:------:|:----------|:------|
|f        |Y       |Z80 binary filename|
|d        |N       |Output directory|Filename directory|
|o        |N       |MZF filename|filename + '.MZF'|
|l        |N       |Load Address (if hex, starts with '$')|$1200|
|s        |N       |Start Address (if hex, starts with '$')|$1200|
|t        |N       |Title (16 characters max)|Filename|
|c        |N       |Comments (104 characters max)||

__Use__

Example:

`java -jar bin2mzf -f file.bin -o game.mzf -l $1200 -s $1400` 
