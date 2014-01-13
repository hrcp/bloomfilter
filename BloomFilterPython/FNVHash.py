class FNVHasher:
    _offset_basis=2166136261;
    _FNV_prime=16777619;
    def hash_string(self,string_to_hash):
        result=self._offset_basis;
        for chunk in string_to_hash:
            result=result^ord(chunk);
            result=result*self._FNV_prime;
            result=result%4294967296; #osigurava aritmetiku u bazi broja 2^32              
        return result

        
