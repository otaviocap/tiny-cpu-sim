![Badge em Desenvolvimento](http://img.shields.io/static/v1?label=STATUS&message=EM%20DESENVOLVIMENTO&color=GREEN&style=for-the-badge)

# TinyCPU Simulator

TinyCPU-Sim is an JavaFX-based open-source project for an instruction-level simulator for the teaching-purpose central processing unit Tiny CPU.

![Screenshot - TinyCPU-Sim](https://github.com/fmsampaio/tiny-cpu-sim/assets/27533879/7c599e9f-26e4-4bd3-8cad-331512c2927c)

### Main functionalities:
- Graphical simulation environment
- Instruction manager interface
- Step-by-step simulation interface
- Load and save memories states from/to files

### Adopted languagens and libraries:
- `Java 8`
- `JavaFX`

## TinyCPU - A teaching-purpose central processing unit

### RTL-Level Schematic

![TinyCPU - Block diagram (en)](https://github.com/fmsampaio/tiny-cpu-sim/assets/27533879/a1aae487-6586-41e8-8eab-834510e5b881)


### Basic Specification

- 8-bit processor
- Two general-purpose registers: RA and RB 
- Memory addessing mode: direct
- Arithmetic and logic unit (ALU):
  - 8-bit 2-complement arithmetic operations
  - Adding (SelULA=0) and subtraction (SelULA=1) operations
  - Condition codes: Z (zero) and N (negative)

### Instruction Set Architecture

| Instruction | Adressing mode | Assembly |  Execution |
| ----------- | -------- | ---------- | -- |
| **LDR** (load register) | Direct | LDR _reg_ _mem_ | _reg_ ← DATA_MEM\[_mem_\] |
| **STR** (store register) | Direct | STR _reg_ _mem_ | DATA_MEM\[_mem_\] ← _reg_ |
| **ADD** (arithmetic add) | Direct | ADD _reg_ _mem_ | _reg_ ← _reg_ + DATA_MEM\[_mem_\] |
| **SUB** (arithmetic subtraction) | Direct | SUB _reg_ _mem_ | _reg_ ← _reg_ - DATA_MEM\[_mem_\] |
| **JMP** (unconditional jump) | Direct | JMP _mem_ | PC ← _mem_ |
| **JC** (conditional jump) | Direct | JC _cc_ _mem_ | (PC ← _mem_) if (_cc_ = Z and Z = 1) or (_cc_ = N and N = 1) |
| **HLT** (halt) | | HLT | _suspend execution_ |



