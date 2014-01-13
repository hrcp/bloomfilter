from FNVHash import FNVHasher
from MurmurHash import MurmurHasher
from math import log,sqrt,fabs,ceil
class BloomFilter:
    _m=0
    _k=0
    _fnv=FNVHasher()
    _murmur=MurmurHasher()
    _bit_arrays=[]
    elements_in_filter=0
    def initialize_using_mk(self,m,k):
        self._m=m
        self._k=k
        print "Created bloom filter with m="+str(m)+" and k="+str(k)+"!"
        for i in range(1,k+1):
            self._bit_arrays.append([0]*m)

    def initialize_using_np(self,n,p):#n predstavlja ocekivani broj unosa u filter, a p dozvoljenu vjerovatnost false positive slucajeva
        k=log(1/p,2)
        m=((fabs(log(p)))*n)/(sqrt(log(2)))
        self.initialize_using_mk(int(m)+1,int(k)+1)
        
        
    def add(self,key):#dodaje key u bloom filter
        fnv=self._fnv.hash_string(key)      #prvo se izracunavaju hashovi kako bi se smanji broj
        murmur=self._murmur.hash_string(key)#izracunavanja pri velikim k-ovima
        for i in range(1,self._k+1):
            index=0
            if(i==1):
                index=fnv%self._m
            elif(i==2):
                index=murmur%self._m
            else:
                index=(fnv+(murmur*i))%self._m
            self._bit_arrays[i-1][index]=1
        self.elements_in_filter+=1
        

    def query(self,key):#provjerava da li je key u bloom filteru. False-nije, True-mozda
        is_there=False
        fnv=self._fnv.hash_string(key)
        murmur=self._murmur.hash_string(key)
        for i in range(1,self._k+1):
            index=0
            if(i==1):
                index=fnv%self._m
            elif(i==2):
                index=murmur%self._m
            else:
                index=(fnv+(murmur*i))%self._m
            if(self._bit_arrays[i-1][index]==1):
                is_there=True
            else:
                is_there=False
                break
        return is_there
