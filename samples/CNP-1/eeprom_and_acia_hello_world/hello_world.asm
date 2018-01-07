                .setcpu "65c02"

		 ACIA_BASE = $4100
                 ACIA_DATA = ACIA_BASE
                 ACIA_STATUS = ACIA_BASE+1
                 ACIA_COMMAND = ACIA_BASE+2
                 ACIA_CONTROL = ACIA_BASE+3

                 .segment "VECTORS"

                 .word   nmi
                 .word   reset
                 .word   irq

                 .code

reset:           jmp main

nmi:             rti

irq:             rti

main:
init_acia:       lda #%00001011				;No parity, no echo, no interrupt
                 sta ACIA_COMMAND
                 lda #%00011111				;1 stop bit, 8 data bits, 19200 baud
                 sta ACIA_CONTROL

write:           ldx #0
next_char:
wait_txd_empty:  lda ACIA_STATUS
                 and #$10
                 beq wait_txd_empty
                 lda text,x
                 beq read
                 sta ACIA_DATA
                 inx
                 jmp next_char
read:
wait_rxd_full:	 lda ACIA_STATUS
                 and #$08
                 beq wait_rxd_full
                 lda ACIA_DATA
                 jmp write

text:            .byte "Hello World!",$0d,$0a,$00

