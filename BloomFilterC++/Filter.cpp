#include "Filter.h"

bool Filter::addElement(std::string newElement)
{
	// calculate fnv hash of new element -> get one bit
	// calculate murmur hash -> get second bit
	// raise calculated bits in filterArray
	uint32_t fnv_hash = fnv(newElement);
	uint32_t m_hash = murmur(newElement);

	filterArray[fnv_hash] = 1;
	filterArray[m_hash] = 1;

	//if there are more hashes than just two
	if (hashNo>2)
	{
		for (int i=3; i<=hashNo; i++)
		{	//"mock" multiple hashes using fnv and murmur
			uint32_t h_i = fnv_hash + i * m_hash;
			h_i %= filterSize;
			filterArray[h_i] = 1;
		}
	}

	return true;
}

bool Filter::queryElement(std::string element)
{
	// calculate fnv hash
	// calculate murmu hash
	// check filterArray for calculated bits
	// if both raised, element PROBABLY exists
	bool found = false;
	
	uint32_t fnv_hash = fnv(element);
	uint32_t m_hash = murmur(element);
	
	if (filterArray[fnv_hash]==1 && filterArray[m_hash]==1)
	{
		found = true;
	}

	//if there are more hashes than just two
	if (hashNo>2)
	{
		for (int i=3; i<=hashNo; i++)
		{
		//"mock" multiple hashes using fnv and murmur
			uint32_t h_i = fnv_hash + i * m_hash;
			h_i %= filterSize;
			if (filterArray[h_i] != 1)
				found=false;
		}
	}
	return found;
}

uint32_t Filter::fnv(std::string element)
{
	// fnv implementation	
	uint32_t fnv_prime=16777619;
	uint32_t fnv_offset = 2166136261;
	int n =element.length();
	uint32_t hash = 0;

	hash = fnv_offset;
	for (int i=0; i<n; i++)
	{
		hash = hash^element[i];
		hash = hash*fnv_prime;
	}

	return hash%filterSize;
}

uint32_t Filter::murmur(std::string element)
{
	// murmur implementation
	uint32_t seed = 0; //just gonna mock this for start, no idea what would be good value
	uint32_t hash;
	uint32_t c1 = 0xcc9e2d51;
	uint32_t c2 = 0x1b873593;
	int r1 = 15;
	int r2 = 13;
	int m =5;
	uint32_t n= 0xe6546b64;
	uint32_t k;

	hash = seed;
	int	length = element.length(); 
	for (int i=0; i<length; i++)
	{
		k = element[i];
		k *= c1;
		k = (k<<r1) | (k>>(32-r1));
		k *= c2;

		hash = hash^k;
		hash = (hash << r2) | (hash >> r2);
		hash = hash*m + n;
	}

	hash *= 0x85ebca6b;
	hash ^= hash >> 13;
	hash *= 0xc2b2ae35;
	hash ^= hash >> 16;
	
	return hash%filterSize;
}


Filter::Filter()
{
}

void Filter::SetAll (int size, int k)
{
	filterSize = size;
	filterArray = new int[size]();
	hashNo = k;
}


Filter::~Filter(void)
{
}
