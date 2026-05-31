#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listDir(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const fullPath = join(dir, file);
      const stat = await fs.stat(fullPath);
      if (stat.isDirectory()) {
         console.log(`📁 ${fullPath}`);
         await listDir(fullPath);
      } else {
         console.log(`📄 ${fullPath} (${stat.size} bytes)`);
      }
    }
  } catch (err) {}
}

console.log("Listing current directory (.) recursively:");
await listDir('.');
