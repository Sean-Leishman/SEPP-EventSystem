#=========================================================================================================
# COURSEWORK 1: TASK 5
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
input_file_name:        .asciiz  "task5.txt"
newline:                .asciiz  "\n"
space:                  .asciiz  " "

#---------------------------------------------------------------------------------------------------------
# GLOBAL VARIABLES IN MEMORY
#---------------------------------------------------------------------------------------------------------
buffer:			.space 16       # A buffer used for file reading. There should be four, three-digit
                                        # numbers inside the file (which parameterise the Mibonacci sequence
                                        # to consider), separated by three spaces and terminated with a newline
sequence:		.align 2
			.space 404

n:                      .align 2
                        .space 4

#=========================================================================================================
# TEXT SEGMENT
#=========================================================================================================
.text

#---------------------------------------------------------------------------------------------------------
# MI PRIME FUNCTION
#---------------------------------------------------------------------------------------------------------
mi_prime_loop:
	sle $t0, $s2, $s1
	beq $t0, $zero, end
	
	li $s4, 1
	li $s3, 0
	
	sge $t0, $s2, 3
	beq $t0, 1, add_to_sequence
	
	# while index<counter && prime_bool == 1
	mi_prime_loop_2:
	sge $t0,$s3, $s2
	seq $t1, $s4, 0
	or $t2, $t0, $t1
	beq $t2, 1, after_loop
	
	# sequence[index]
	sll $t0, $s3, 2
	add $t0, $t0, $a1
	lw $s5, 0($t0)
	
	sgt $t0, $s5, 1
	beq $t0, 1, inner_if_1
	
	return_here:
	add $s3, $s3, 1
	j mi_prime_loop_2
	
	print_st:
	sll $t0, $s2, 2
	add $t0, $t0, $a1
	li $v0, 1
	lw $a0, 0($t0)
	syscall
	
	li $a0, 32
    	li $v0, 11  
    	syscall
    	
    	add $s2, $s2, 1
    	j mi_prime_loop
    	
inner_if_1:
# Address of store 
	sll $t0, $s2, 2
	add $t0, $t0, $a1
	lw $s6, 0($t0)
	
	sne $t0, $s6, $s5
	beq $t0, 1, inner_if_2
	j return_here
inner_if_2:
	div $s6,$s5
	mfhi $t0
	beq $t0, $zero, assignment
	j return_here
assignment:
	li $s4, 0
	j return_here
	
after_loop:
	beq $s4, 1, print_st
	before_after_loop:
	add $s2, $s2, 1
	j mi_prime_loop
add_to_sequence:
	# Address of store 
	sll $t0, $s2, 2
	add $t1, $t0, $a1
	
	# Address of n-1
	sub $t2, $t1, 4
	lw $t3, 0($t2)
	
	# Address of n-2
	sub $t4, $s2, 2
	div $t4, $t4, 4
	mfhi $t4
	
	sll $t4, $t4, 2
	add $t5, $a1, $t4 
	lw $t6, 0($t5)
	
	# Value of M(n) in $t7
	move $t7,$zero
	add $t7, $t3, 1
	add $t7, $t7, $t6
	
	sw $t7, 0($t1)
	
	j mi_prime_loop_2
	
end:	
	la $a0, newline
        li $v0, 4
        syscall 
	jr $ra
mi_prime:
        # TODO: Implement me!
        la $a1, sequence
        la $a2, n
        
        lw $s0, 0($a1)
        lw $s1, 0($a2)
        
        li $s2, 0 # counter
        li $s3, 0 #index
        li $s4, 0 # prime_bool
        
        j mi_prime_loop
        jr $ra

#---------------------------------------------------------------------------------------------------------
# MAIN CODE BLOCK (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
.globl main                             # Declare main label as globally visible
main:                                   # This is where the program will start executing

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
        li $a2, 16                      # Read entire file into buffer
        syscall
                                        # An implementation of the C atoi() function, which
atoi:                                   # converts a character string into an integer
        move $t0, $0                    # Buffer index
        move $t1, $0                    # Sequence index
        move $t2, $0                    # Current integer value

atoi_loop:
        lb $t3, buffer($t0)             # Read next character

        li $t4, 48                      # A non-integer character marks the end of this
        blt $t3, $t4, store             # number, so store it and then reset the value
        li $t4, 57
        bgt $t3, $t4, store

        subi $t3, $t3, 48               # Convert ASCII value to integer value
        mul $t2, $t2, 10                # Current int = current int * 10 + new digit
        add $t2, $t2, $t3

	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store:
        li $t4, 12                      # The fourth and final parameter is n, which needs to be stored elsewhere
        beq $t1, $t4, store_n

	sw $t2, sequence($t1)
	addi $t1, $t1, 4                # Increment sequence index

	move $t2, $0                    # Reset current integer value
	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

store_n:
        sw $t2, n                       # Store the value of n, and then end the function

read_file_end:
        li $v0, 16                      # System call for close file
        move $a0, $s0                   # File descriptor to close
        syscall

        jal mi_prime                    # miPrime();

#---------------------------------------------------------------------------------------------------------
# EXIT PROGRAM (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
main_end:
        li $v0, 10
        syscall

#---------------------------------------------------------------------------------------------------------
# END OF CODE
#---------------------------------------------------------------------------------------------------------
