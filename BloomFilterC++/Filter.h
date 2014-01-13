#pragma once
#include<string>
#include <stdint.h>

class Filter
{
public:
	int filterSize;
	int* filterArray;
	int hashNo;

	Filter();
	~Filter(void);

	void SetAll (int size, int k);
	void setSize(int size);

	bool addElement (std::string);

	bool queryElement (std::string);

private:
	uint32_t fnv(std::string);
	uint32_t murmur(std::string);
};