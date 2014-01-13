// BioTest.cpp : Defines the entry point for the console application.
//

//#include "stdafx.h"
//#include <tchar.h>
#include <iostream>
#include <fstream>
#include <string>
#include <cmath>
#include <ctime>
#include "Filter.h"

#define probability 0.2
#define exp_num 1000
#define arraySize 200

using namespace std;

int calculateSize (double p, int n)
{
	int m = n*abs(log(p)) / pow(log(2),2);

	return m;
}

int calculateHashNumber (double p)
{
	double k1 = log10(1/p);
	int k = k1/log10(2);

	if(k<2)
		k=2;

	return k;
}


int main(int argc, char* argv[])
{
	//ifstream file;
	//file.exceptions ( ifstream::failbit | ifstream::badbit );
	string line="";
	Filter filter;
	bool flag = true;
	//bool fastaFlag = false;

	// check number of arguments -DONE
	// if zero, explain usage -DONE
	// if one text file, it loads elements and lets you query via string -IN PROGRESS
	// if two text files, first is list of elements, the second one is element to query
	// -f option for FASTA format
	if (argc==1 || argc>4)
	{
		string message = "Welcome to BioBloom\n\nPlease use the following format:\nbiobloom \"filename.txt\"  -- filename.txt should be a text file with elements you wish to load; the program than allows you to query string elements\n\nbiobloom -f \"filename.txt\"  -- same as before, but the content in filename.txt should be in FASTA format\n\nbiobloom \"filename.txt\" \"queryfilename.txt\"  -- filename.txt should be a text file with elements you wish to load; queryfilename.txt should hold the element you wish to query\n\nbiobloom -f \"filename.txt\" \"queryfilename.txt\"  -- -f option defines that content of BOTH text files should be in FASTA format";
		cout << message;
	}

	if(argc==2)
	{
		//cout << argv[1];
		ifstream file(argv[1]); //open the file
		if (file.is_open() && file.good())
		{
			cout << "Processing file...\n";
			string line = "";

			int wordNumber=0;;
			while(getline(file, line))
			{
				wordNumber++;
			}

			file.close();
			ifstream file(argv[1]);
			clock_t startTime = clock();
			while (getline(file, line))
			{
				if(flag)
				{
					double p=2;
					if (line[0]=='?')
					{
						int p1 = line[1] - '0';
						int p2 = line[2] - '0';
						if (p2>0)
						{
							p = p1*10+p2;
							p = p/100;
						}
						else
							p = p1/100.;
					}
					if (p<0 || p>1)
					{
						p=probability;
					}

					int m_array = calculateSize(p, wordNumber);
					int k_hash = calculateHashNumber(p);

					//if (m_array>65535) m_array = 65535;

					cout << m_array << " " << k_hash << " " << m_array*sizeof(bool) << endl;
					
					filter.SetAll(m_array, k_hash);
					flag = false;
					if (line[0]!='?')
					{
						filter.addElement(line);
					}
				}
				else if(filter.addElement(line))
				{
					//cout << "Element "+line+" added\n";
				}
			}
			file.close();
			clock_t endTime = clock();
			clock_t clockTicksTaken = endTime - startTime;
			double timeInSeconds = clockTicksTaken / (double) CLOCKS_PER_SEC;

			cout<<"Time taken to make filter array: "<<timeInSeconds<<endl<<endl;

			cout << "Filter array is ready, please enter the query, or :Q to quit.\n\n";
			string query="";
			while(query!=":Q")
			{
				//cout<<sizeof(uint32_t)<<endl;
				//cout<<sizeof(char)<<endl;
				//cout<<sizeof(query[0])<<endl;

				cout << "Query: ";
				cin >> query;
				clock_t startTimeQ = clock();
				if (filter.queryElement(query))
					cout << "Element "+query+" is probably here!\n";
				else
					cout << "Element "+query+" is not here\n";

				clock_t endTimeQ = clock();
				clock_t clockTicksTakenQ = endTimeQ - startTimeQ;
				double timeInSecondsQ = clockTicksTakenQ / (double) CLOCKS_PER_SEC;
				cout << "Time taken for query: "<<timeInSecondsQ<<endl<<endl;
			}
		}
		else
		{
			cout << "Problem opening file. Please check file name and try again.\n";
		}
	}
	else if(argc==3)
	{
		if (argv[1] == "-f")
		{
			ifstream file(argv[2]); //open the file
			if (file.is_open() && file.good())
			{
				clock_t startTime = clock();

				cout << "Processing file...\n";
				string line = "";

				int wordNumber=0;;
				while(getline(file, line))
				{
					wordNumber++;
				}
				file.close();
				ifstream file(argv[1]);

				while (getline(file, line))
				{
					if(flag)
					{
						int p1 = line[0] - '0';
						int p2 = line[1] - '0';
						double p = p1*10+p2;
						p = p/100;

						int m_array = calculateSize(p, wordNumber);
						int k_hash = calculateHashNumber(p);

						cout << m_array << " " << k_hash << endl;
						filter.SetAll(m_array, k_hash);
						flag = false;
					}
					else if(line[0]=='>')
					{
						continue;
					}
					else
					{
						if(filter.addElement(line))
						{
							cout << "Element "+line+" added\n";
						}
					}
				}

				clock_t endTime = clock();
				clock_t clockTicksTaken = endTime - startTime;
				double timeInSeconds = clockTicksTaken / (double) CLOCKS_PER_SEC;

				cout<<"Time taken to make filter array: "<<timeInSeconds<<endl<<endl;

				cout << "Filter array is ready, please enter the query, or :Q to quit.\n\n";
				string query="";
				while(query!=":Q")
				{
					cout << "Query: ";
					cin >> query;
					clock_t startTimeQ = clock();
					if (filter.queryElement(query))
						cout << "Element "+query+" is probably here!\n";
					else
						cout << "Element "+query+" is not here\n";

					clock_t endTimeQ = clock();
					clock_t clockTicksTakenQ = endTimeQ - startTimeQ;
					double timeInSecondsQ = clockTicksTakenQ / (double) CLOCKS_PER_SEC;
					cout << "Time taken for query: "<<timeInSecondsQ<<endl<<endl;
				}
			}
			else
			{
				cout << "Problem opening file. Please check file name and try again.\n";
			}
		}
		else
		{
			/** TWO FILES; SECOND IS QUERY; COUT<<MAYBE OR NO **/
			ifstream file(argv[1]); //open the file
			ifstream file2(argv[2]); //query file
			if (file.is_open() && file.good() && file2.is_open()&& file2.good())
			{
				clock_t startTime = clock();

				cout << "Processing file...\n";
				string line = "";

				int wordNumber=0;;
				while(getline(file, line))
				{
					wordNumber++;
				}
				file.close();
				ifstream file(argv[1]);

				while (getline(file, line))
				{
					if(flag)
					{
						int p1 = line[0] - '0';
						int p2 = line[1] - '0';
						double p = p1*10+p2;
						p = p/100;

						int m_array = calculateSize(p, wordNumber);
						int k_hash = calculateHashNumber(p);

						cout << m_array << " " << k_hash << endl;
						filter.SetAll(m_array, k_hash);
						flag = false;
					}
					else if(filter.addElement(line))
					{
						cout << "Element "+line+" added\n";
					}
				}

				clock_t endTime = clock();
				clock_t clockTicksTaken = endTime - startTime;
				double timeInSeconds = clockTicksTaken / (double) CLOCKS_PER_SEC;

				cout<<"Time taken to make filter array: "<<timeInSeconds<<endl<<endl;

				cout << "\nProcessing query...\n";
				string file_query="";
				getline(file2, file_query);
				
				clock_t startTimeQ = clock();
				
				if (filter.queryElement(file_query))
						cout << "Element "+file_query+" is probably here!\n";
					else
						cout << "Element "+file_query+" is not here\n";

				clock_t endTimeQ = clock();
				clock_t clockTicksTakenQ = endTimeQ - startTimeQ;
				double timeInSecondsQ = clockTicksTakenQ / (double) CLOCKS_PER_SEC;
				cout << "Time taken for query: "<<timeInSecondsQ<<endl<<endl;
			}
			else
			{
				cout << "Problem opening file. Please check file name and try again.";
			}
		}
	}
	else if(argc==4)
	{
		if (argv[1]!="-f")
		{
			cout << "Error! Type \"biobloom.exe\" for help.";

		}
		else
		{
			/**TWO FILES; BOTH IN FASTA FORMAT; SIMPLE COUT MAYBE OR NO **/
			ifstream file(argv[2]); //open the file
			ifstream file2(argv[3]); //query file
			if (file.is_open() && file.good() && file2.is_open()&& file2.good())
			{
				clock_t startTime = clock();

				cout << "Processing file...\n";
				string line = "";

				int wordNumber=0;;
				while(getline(file, line))
				{
					wordNumber++;
				}
				file.close();
				ifstream file(argv[1]);

				while (getline(file, line))
				{
					if(flag)
					{
						int p1 = line[0] - '0';
						int p2 = line[1] - '0';
						double p = p1*10+p2;
						p = p/100;

						int m_array = calculateSize(p, wordNumber);
						int k_hash = calculateHashNumber(p);

						cout << m_array << " " << k_hash << endl;
						filter.SetAll(m_array, k_hash);
						flag = false;
					}
					else if(filter.addElement(line))
					{
						cout << "Element "+line+" added\n";
					}
				}

				clock_t endTime = clock();
				clock_t clockTicksTaken = endTime - startTime;
				double timeInSeconds = clockTicksTaken / (double) CLOCKS_PER_SEC;

				cout<<"Time taken to make filter array: "<<timeInSeconds<<endl<<endl;

				cout << "Processing query...\n";
				string file_query="";
				getline(file2, file_query);

				clock_t startTimeQ = clock();

				if (filter.queryElement(file_query))
						cout << "Element "+file_query+" is probably here!\n";
				else
						cout << "Element "+file_query+" is not here\n";
				
				clock_t endTimeQ = clock();
				clock_t clockTicksTakenQ = endTimeQ - startTimeQ;
				double timeInSecondsQ = clockTicksTakenQ / (double) CLOCKS_PER_SEC;
				cout << "Time taken for query: "<<timeInSecondsQ<<endl<<endl;
			}
			else
			{
				cout << "Problem opening file. Please check file name and try again.";
			}
		}
	}
	return 0;
}

