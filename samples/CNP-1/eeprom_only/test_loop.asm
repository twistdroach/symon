	.setcpu "65c02"
	.segment "VECTORS"

	.word	loop
	.word	loop
	.word	loop

	.code
loop:	lda #$12
	jmp loop
