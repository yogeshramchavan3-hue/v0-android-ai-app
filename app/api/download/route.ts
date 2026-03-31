import { NextResponse } from "next/server"
import JSZip from "jszip"
import { kaliAIProject } from "@/lib/kali-ai-project"

export async function GET() {
  const zip = new JSZip()

  // Add all project files to the ZIP
  for (const file of kaliAIProject) {
    zip.file(`KaliAI/${file.path}`, file.content)
  }

  // Add gradle wrapper files
  zip.file(
    "KaliAI/gradle/wrapper/gradle-wrapper.properties",
    `distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists`
  )

  // Add .gitignore
  zip.file(
    "KaliAI/.gitignore",
    `*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties`
  )

  // Generate the ZIP file
  const zipContent = await zip.generateAsync({ type: "arraybuffer" })

  return new NextResponse(zipContent, {
    headers: {
      "Content-Type": "application/zip",
      "Content-Disposition": "attachment; filename=KaliAI-Android-Project.zip",
    },
  })
}
