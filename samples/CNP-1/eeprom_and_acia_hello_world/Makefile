CA=ca65
LD=ld65

PGM=hello_world

all: $(PGM)

$(PGM): $(PGM).o
	$(LD) -C cnp-2.cfg -vm -m $(PGM).map -o $(PGM).rom $(PGM).o

$(PGM).o:
	$(CA) --listing $(PGM).lst -o $(PGM).o $(PGM).asm

clean:
	rm -f *.o *.rom *.map *.lst

flash: clean all
	minipro -p at28c256 -w $(PGM).rom
