1. createIdContextFile - Read the xml files and create a single file 
	with id, context
	It resulted in around 9.45mn rows
2. editFile - Read the single file and create separate files for each id and appended the context to form a single context for each id.
	It resulted around 2.2mn files
3. Read each of this files and store a file
	with id, concatenated context