class MurmurHasher:    
    def hash_string(self,string_to_hash):
        c1 = 0xcc9e2d51
        c2 = 0x1b873593
        r1 = 15
        r2 = 13
        m = 5
        n = 0xe6546b64
        seed = 0
        lenght=len(string_to_hash)
        h = seed
        lenght4=lenght/4

        for i in range(0,lenght4):
          #little endian load order
            k=(ord(string_to_hash[i+3])<<24)^(ord(string_to_hash[i+2])<<16)^(ord(string_to_hash[i+1])<<8)^(ord(string_to_hash[i]))
            k *= c1
            k=k%4294967296#sve ovakve naredbe osiguravaju rad u artimetici broja 2^32
            k = (k << r1) | (k >> 32-r1)# ROTL32(k1,r1)
            k *= c2
            k=k%4294967296                                                                                                          
            h ^= k                                                                                                          
            h = (h << r2) | (h >> 32-r2)#ROTL32(h1,r2)
            h = h*5+0xe6546b64
            k=k%4294967296

        k=0

        if((lenght & 0x03)==3):
            k=ord(string_to_hash[lenght4 + 2])<<16

        if((lenght & 0x03)>=2):
            k=k^(ord(string_to_hash[lenght4 + 1])<<8)
        if((lenght & 0x03)>=1):
            k=k^(ord(string_to_hash[lenght4]))
            k *= c1
            k=k%4294967296
            k = (k<<r1) | (k>>32-r1)# ROTL32(k1,15);
            k *= c2
            k=k%4294967296
            h ^= k        

        #32bitna finalizacija
        h^= lenght
        h^= h >> 16
        h*= 0x85ebca6b
        h=h%4294967296
        h^= h>>13
        h*=0xc2b2ae35;
        h=h%4294967296
        h^= h >> 16

        return h%4294967296

