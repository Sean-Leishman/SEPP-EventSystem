#=========================================================================================================
# COURSEWORK 1: TASK 3
#=========================================================================================================
# Inf2 Computer Systems
# Kim Stonehouse
# 4 October 2021

#=========================================================================================================
# DATA SEGMENT
#=========================================================================================================
.data

#---------------------------------------------------------------------------------------------------------
# CONSTANT STRINGS
#---------------------------------------------------------------------------------------------------------
input_file_name:        .asciiz  "task3.txt"
newline:                .asciiz  "\n"
space:                  .asciiz  " "

#---------------------------------------------------------------------------------------------------------
# GLOBAL VARIABLES IN MEMORY
#---------------------------------------------------------------------------------------------------------
buffer:			.space 412      # A buffer used for file reading. The buffer file contains Mib(0),
                                        # Mib(1) and Mib(2) followed by spaces, and then an input sequence.
                                        # The maxiumum input sequence length is 100 three-digit integers,
                                        # 99 spaces between them and one newline to end

input:		        .align 2        # The input sequence of numbers to encrypt
			.space 404

input_length:           .align 2        # The actual number of terms in the input sequence
                        .space 4

sequence:		.align 2        # The maximum size of n is 100, so the maximum length of the sequence
			.space 408	# is 101 integers, plus a -1 to mark the end of the sequence

#=========================================================================================================
# TEXT SEGMENT
#=========================================================================================================
.text

#---------------------------------------------------------------------------------------------------------
# ENCRYPT FUNCTION
#---------------------------------------------------------------------------------------------------------
mibonacci_number:
	# Address of store 
	sll $t0, $s3, 2
	add $t1, $t0, $a2
	
	# Address of n-1
	sub $t2, $t1, 4
	lw $t4, 0($t2)
	
	# Address of n-2
	sub $t6, $s3, 2
	div $t6, $t6, 4
	mfhi $t6
	
	sll $t6, $t6, 2
	add $t3, $a2, $t6 
	lw $t5, 0($t3)
	
	# Value of M(n) in $t7
	move $t7,$zero
	add $t7, $t4, 1
	add $t7, $t7, $t5
	
	sw $t7, 0($t1)
	
	jr $ra

conditional:
	# Address of store 
	sll $t0, $s3, 2
	add $t1, $t0, $a3
	
	li $v0, 1
	lw $a0, 0($t1)	
	syscall
	
	li $a0, 32
    	li $v0, 11  
    	syscall
    	
    	add $s4, $s4, 1
    	j return_here

encrypt_loop:
	li $t5, 100
	sub $t0, $s3, 1
	sge $t1, $t5, $t0
	sge $t2, $s1, $s4
	and $t1, $t1, $t2
	beq $t1, $zero, end 
	
	#ADD IF COUNTER>3THEN DO SEQUENCE
	sle $t0, $s3, 2
	beq $t0, $zero, add_to_sequence
	return_here_add_to_sequence:
	
	sll $t0, $s4, 2
	add $t0, $t0, $a1
	lw $t2, 0($t0)

	beq $t2, $s3, conditional
	return_here:
	add $s3, $s3, 1
	j encrypt_loop

add_to_sequence:
# Address of store 
	sll $t0, $s3, 2
	add $t1, $t0, $a3
	
	# Address of n-1
	sub $t2, $t1, 4
	lw $t4, 0($t2)
	
	# Address of n-2
	sub $t6, $s3, 2
	div $t6, $t6, 4
	mfhi $t6
	
	sll $t6, $t6, 2
	add $t3, $a3, $t6 
	lw $t5, 0($t3)
	
	# Value of M(n) in $t7
	move $t7,$zero
	add $t7, $t4, 1
	add $t7, $t7, $t5
	
	sw $t7, 0($t1)
	lw $s7,0($t1)
	j return_here_add_to_sequence
encrypt:
	la $a1, input
	la $a2, input_length
	la $a3, sequence
	
	lw $s0, 0($a1)
	lw $s1, 0($a2)
	lw $s2, 0($a3)
	
	li $s3, 0 #counter
	li $s4, 0 # index
	
	j encrypt_loop 
        
	jr $ra

end:
	la $a0, newline
        li $v0, 4
        syscall 
	jr $ra
#---------------------------------------------------------------------------------------------------------
# MAIN CODE BLOCK (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
.globl main                             # Declare main label as globally visible
main:                                   # This is where the program will start executing
	j read_file
#---------------------------------------------------------------------------------------------------------
# READ INTEGER FILE FUNCTION (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
read_file:
        li $v0, 13                      # System call code for open file
        la $a0, input_file_name         # Load address of the input file name
        li $a1, 0                       # Flag for reading
        li $a2, 0                       # Mode is ignored
        syscall

        move $s0, $v0                   # Save the file descriptor returned by syscall

	li $v0, 14                      # System call code for reading from file
        move $a0, $s0                   # File descriptor
        la $a1, buffer                  # Load address of buffer to write into
        li $a2, 412                     # Read entire file into buffer
        syscall
                                        # An implementation of the C atoi() function, which
atoi:                                   # converts a character string into an integer
        move $t0, $0                    # Buffer index
        move $t1, $0                    # Input index
        move $t2, $0                    # Current integer value
        move $t3, $0                    # Sequence index

atoi_loop:
        lb $t4, buffer($t0)             # Read next character

        li $t5, 48                      # A non-integer character marks the end of this
        blt $t4, $t5, store             # number, so store it and then reset the value
        li $t5, 57
        bgt $t4, $t5, store

        subi $t4, $t4, 48               # Convert ASCII value to integer value
        mul $t2, $t2, 10                # Current int = current int * 10 + new digit
        add $t2, $t2, $t4

	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store:
	li $t5, 12                      # Number is either Mib(0), Mib(1) or Mib(2)
	blt $t3, $t5, store_sequence    # Increment sequence index

store_input:
	sw $t2, input($t1)              # Number is part of the input
	addi $t1, $t1, 4                # Increment input index
	j store_end

store_sequence:
	sw $t2, sequence($t3)
	addi $t3, $t3, 4

store_end:
	lb $t5, newline                 # A newline marks the end of the sequence
	beq $t4, $t5, read_file_end

	move $t2, $0                    # Reset current integer value
	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

read_file_end:
        li $t0, -1                      # Terminate the array with -1
	sw $t0,  input($t1)

	div $t1, $t1, 4                 # Store sequence length
	sw $t1, input_length

        li $v0, 16                      # System call for close file
        move $a0, $s0                   # File descriptor to close
        syscall

        jal encrypt                     # encrypt();

#---------------------------------------------------------------------------------------------------------
# EXIT PROGRAM (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
main_end:
        li $v0, 10
        syscall

#---------------------------------------------------------------------------------------------------------
# END OF CODE
#---------------------------------------------------------------------------------------------------------
