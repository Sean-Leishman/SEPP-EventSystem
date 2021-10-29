#=========================================================================================================
# COURSEWORK 1: TASK 1
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
input_file_name:        .asciiz  "task1.txt"
newline:                .asciiz  "\n"
space:                  .asciiz  " "

#---------------------------------------------------------------------------------------------------------
# GLOBAL VARIABLES IN MEMORY
#---------------------------------------------------------------------------------------------------------
buffer:			.space 400      # A buffer used for file reading. The maxiumum
                                        # sequence length is 100 three-digit integers,
                                        # 99 spaces between them and one newline to end

sequence:               .align 2        # The maximum length of the sequence is 100 integer
                        .space 404      # terms, plus a -1 to mark the end of the sequence

sequence_length:        .align 2        # The actual number of terms in the sequence
	                .space 4

#=========================================================================================================
# TEXT SEGMENT
#=========================================================================================================
.text

#---------------------------------------------------------------------------------------------------------
# PRINT SEQUENCE FUNCTION
#---------------------------------------------------------------------------------------------------------
print_loop:
	beq $s1,$s0, end
	
	sll $t0, $s1, 2
	add $t1, $a1, $t0
	
	li $v0, 1
	lw $a0, 0($t1)
	syscall
	
	li $a0, 32
    	li $v0, 11  
    	syscall
	
	addi $s1,$s1,1
	
	j print_loop
end:	
	la $a0, newline
        li $v0, 4
        syscall 
	jr $ra
print_sequence:
        # TODO: Implement me
        la $a1, sequence 
        la $a2, sequence_length 
        
        lw $s0, 0($a2)
        li $s1,0
        lw $s2, 0($a1)
        
        j print_loop
        
        la $a0, newline
        li $v0, 4
        syscall 
        
        jr $ra

#---------------------------------------------------------------------------------------------------------
# REVERSE MIBONACCI FUNCTION
#---------------------------------------------------------------------------------------------------------
reverse_loop:
	slt $t0, $s3, $s0
	beq $t0, $zero, end
	
	# Get address of array[start] and store value at array[start] in temporary $t2
	sll $t1, $s3, 2
	add $t4, $t1, 0
	add $t1,$a1,$t1
	lw $t2, 0($t1)
	
	# Get address of array[end]
	sll $t3,$s0,2
	add $t3, $t3, $a1
	lw $t4, 0($t3)
	
	sw $t4, 0($t1)
	sw $t2, 0($t3)
	
	addi $s3, $s3, 1 
	subi $s0, $s0, 1
	
	j reverse_loop

reverse_mibonacci:
        # TODO: Implement me!
        la $a1, sequence 
        la $a2, sequence_length 
        
        lw $s0, 0($a2)
        sub  $s0, $s0, 1
        li $s1,0
        lw $s2, 0($a1)
        
        li $s3,0
        
       	j reverse_loop
        jr $ra

#---------------------------------------------------------------------------------------------------------
# MAIN CODE BLOCK (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
.globl main                             # Declare main label as globally visible
main:                                   # This is where the program will start executing
	j read_file
	j print_sequence
	
	j reverse_mibonacci
	j print_sequence
	
	# end the program
        li   $v0, 10
        syscall

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
        li $a2, 400                     # Read entire file into buffer
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
	sw $t2, sequence($t1)
	addi $t1, $t1, 4                # Increment sequence index

	lb $t4, newline                 # A newline marks the end of the sequence
	beq $t3, $t4, read_file_end

	move $t2, $0                    # Reset current integer value
	addi $t0, $t0, 1                # Increment buffer index
	j atoi_loop

read_file_end:
        li $t0, -1                      # Terminate the array with -1
	sw $t0,  sequence($t1)

	div $t1, $t1, 4                 # Store sequence length
	sw $t1, sequence_length

        li $v0, 16                      # System call for close file
        move $a0, $s0                   # File descriptor to close
        syscall

        jal reverse_mibonacci           # reverseMibonacci();
        jal print_sequence              # printSequence();

#---------------------------------------------------------------------------------------------------------
# EXIT PROGRAM (DO NOT MODIFY)
#---------------------------------------------------------------------------------------------------------
main_end:
        li $v0, 10
        syscall

#---------------------------------------------------------------------------------------------------------
# END OF CODE
#---------------------------------------------------------------------------------------------------------
