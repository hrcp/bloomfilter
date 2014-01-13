from BloomFilter import BloomFilter
import sys
import time
import resource

if(len(sys.argv)!=4):
    print "Invalid input arguments! The number of them is not right!"
    exit(0)

bloom=BloomFilter()
try:
    input_file=open(sys.argv[1],"r")
    output_file=open(sys.argv[2],"a+")
    p=float(sys.argv[3])

except IOError:
    print "Invalid input arguments! Check that the input file exists!"
    exit(0)
except ValueError as e:
    print "Third argument not a float number!"
    exit(0)
lines=input_file.readlines()
bloom.initialize_using_np(len(lines),float(sys.argv[3]))

start_time=time.time()
for line in lines:
    line=line.strip().rstrip()
    bloom.add(line)

input_file.close()

print "Time needed to fill filter: ", time.time() - start_time,"s"
print "Memory occupied by bloom filter ",resource.getrusage(resource.RUSAGE_SELF).ru_maxrss, "bytes"
while(True):
    inputed=raw_input("Enter key to check if it is in filter: ")
    if(bloom.query(inputed)):
        print "Possible in filter"
        output_file.write("Key "+inputed+" possible in filter\n")
    else:
        print "Not in filter"
        output_file.write("Key "+inputed+" not in filter\n")
output_file.close()
