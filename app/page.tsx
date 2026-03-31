"use client"

import { useState } from "react"
import { kaliAIProject, type ProjectFile } from "@/lib/kali-ai-project"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Download, FileCode, Folder, FolderOpen, Copy, Check, Smartphone, Zap, MessageSquare, Key, Database, Bell } from "lucide-react"

export default function KaliAIProjectPage() {
  const [selectedFile, setSelectedFile] = useState<ProjectFile | null>(kaliAIProject[0])
  const [copied, setCopied] = useState(false)
  const [expandedFolders, setExpandedFolders] = useState<Set<string>>(new Set(["app", "app/src", "app/src/main", "app/src/main/java", "app/src/main/java/com", "app/src/main/java/com/kali", "app/src/main/java/com/kali/ai", "app/src/main/res", "app/src/main/res/layout", "app/src/main/res/values"]))

  const copyToClipboard = async () => {
    if (selectedFile) {
      await navigator.clipboard.writeText(selectedFile.content)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  const downloadZip = () => {
    window.location.href = "/api/download"
  }

  // Build folder structure
  const buildFolderStructure = () => {
    const structure: Record<string, ProjectFile[]> = {}
    
    for (const file of kaliAIProject) {
      const parts = file.path.split("/")
      const folder = parts.length > 1 ? parts.slice(0, -1).join("/") : ""
      if (!structure[folder]) {
        structure[folder] = []
      }
      structure[folder].push(file)
    }
    
    return structure
  }

  const folderStructure = buildFolderStructure()

  const toggleFolder = (folder: string) => {
    const newExpanded = new Set(expandedFolders)
    if (newExpanded.has(folder)) {
      newExpanded.delete(folder)
    } else {
      newExpanded.add(folder)
    }
    setExpandedFolders(newExpanded)
  }

  const getFileIcon = (path: string) => {
    if (path.endsWith(".java")) return "text-orange-500"
    if (path.endsWith(".xml")) return "text-purple-500"
    if (path.endsWith(".gradle")) return "text-green-500"
    if (path.endsWith(".properties") || path.endsWith(".pro")) return "text-gray-500"
    return "text-blue-500"
  }

  const renderFolderTree = () => {
    const folders = Object.keys(folderStructure).sort()
    const renderedFolders = new Set<string>()
    const items: JSX.Element[] = []

    // Root files first
    if (folderStructure[""]) {
      for (const file of folderStructure[""]) {
        items.push(
          <button
            key={file.path}
            onClick={() => setSelectedFile(file)}
            className={`flex items-center gap-2 px-3 py-1.5 text-sm w-full text-left hover:bg-muted/50 rounded transition-colors ${
              selectedFile?.path === file.path ? "bg-primary/10 text-primary font-medium" : ""
            }`}
          >
            <FileCode className={`h-4 w-4 ${getFileIcon(file.path)}`} />
            <span className="truncate">{file.path}</span>
          </button>
        )
      }
    }

    // Then folders
    for (const folderPath of folders) {
      if (folderPath === "") continue

      const parts = folderPath.split("/")
      
      // Render parent folders that haven't been rendered
      for (let i = 0; i < parts.length; i++) {
        const currentPath = parts.slice(0, i + 1).join("/")
        if (!renderedFolders.has(currentPath)) {
          renderedFolders.add(currentPath)
          const depth = i
          const isExpanded = expandedFolders.has(currentPath)
          const folderName = parts[i]

          items.push(
            <button
              key={`folder-${currentPath}`}
              onClick={() => toggleFolder(currentPath)}
              className="flex items-center gap-2 px-3 py-1.5 text-sm w-full text-left hover:bg-muted/50 rounded transition-colors font-medium"
              style={{ paddingLeft: `${12 + depth * 16}px` }}
            >
              {isExpanded ? (
                <FolderOpen className="h-4 w-4 text-amber-500" />
              ) : (
                <Folder className="h-4 w-4 text-amber-500" />
              )}
              <span>{folderName}</span>
            </button>
          )
        }
      }

      // Render files in this folder if expanded
      const isParentExpanded = expandedFolders.has(folderPath)
      if (isParentExpanded && folderStructure[folderPath]) {
        const depth = folderPath.split("/").length
        for (const file of folderStructure[folderPath]) {
          const fileName = file.path.split("/").pop()
          items.push(
            <button
              key={file.path}
              onClick={() => setSelectedFile(file)}
              className={`flex items-center gap-2 py-1.5 text-sm w-full text-left hover:bg-muted/50 rounded transition-colors ${
                selectedFile?.path === file.path ? "bg-primary/10 text-primary font-medium" : ""
              }`}
              style={{ paddingLeft: `${12 + depth * 16}px` }}
            >
              <FileCode className={`h-4 w-4 ${getFileIcon(file.path)}`} />
              <span className="truncate">{fileName}</span>
            </button>
          )
        }
      }
    }

    return items
  }

  const features = [
    { icon: Smartphone, title: "Voice Assistant", desc: "Wake word 'Kali' detection" },
    { icon: MessageSquare, title: "Auto-Reply", desc: "WhatsApp, Telegram, Instagram" },
    { icon: Key, title: "Multi-API", desc: "DeepSeek, GPT, Gemini, Grok" },
    { icon: Database, title: "SQLite Storage", desc: "Permanent chat history" },
    { icon: Zap, title: "Smart Fallback", desc: "Automatic API rotation" },
    { icon: Bell, title: "Notification Listener", desc: "Background auto-reply" },
  ]

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b bg-card sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-gradient-to-br from-violet-500 to-purple-600 flex items-center justify-center">
                <Smartphone className="h-5 w-5 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold">Kali AI</h1>
                <p className="text-xs text-muted-foreground">Android Project - Complete Source Code</p>
              </div>
            </div>
            <Button onClick={downloadZip} className="bg-gradient-to-r from-violet-500 to-purple-600 hover:from-violet-600 hover:to-purple-700">
              <Download className="h-4 w-4 mr-2" />
              Download ZIP
            </Button>
          </div>
        </div>
      </header>

      {/* Features */}
      <section className="border-b bg-muted/30">
        <div className="container mx-auto px-4 py-6">
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {features.map((feature, i) => (
              <div key={i} className="flex items-center gap-3 p-3 rounded-lg bg-card border">
                <feature.icon className="h-5 w-5 text-violet-500 shrink-0" />
                <div className="min-w-0">
                  <p className="text-sm font-medium truncate">{feature.title}</p>
                  <p className="text-xs text-muted-foreground truncate">{feature.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6">
        <div className="grid lg:grid-cols-[300px_1fr] gap-6 h-[calc(100vh-280px)]">
          {/* File Tree */}
          <Card className="overflow-hidden">
            <CardHeader className="py-3 px-4 border-b bg-muted/30">
              <CardTitle className="text-sm font-medium flex items-center gap-2">
                <Folder className="h-4 w-4 text-amber-500" />
                Project Files ({kaliAIProject.length} files)
              </CardTitle>
            </CardHeader>
            <ScrollArea className="h-[calc(100%-52px)]">
              <div className="p-2">
                {renderFolderTree()}
              </div>
            </ScrollArea>
          </Card>

          {/* Code Viewer */}
          <Card className="overflow-hidden">
            <CardHeader className="py-3 px-4 border-b bg-muted/30 flex flex-row items-center justify-between">
              <CardTitle className="text-sm font-medium flex items-center gap-2">
                <FileCode className={`h-4 w-4 ${selectedFile ? getFileIcon(selectedFile.path) : ""}`} />
                {selectedFile?.path || "Select a file"}
              </CardTitle>
              {selectedFile && (
                <Button variant="outline" size="sm" onClick={copyToClipboard}>
                  {copied ? (
                    <>
                      <Check className="h-4 w-4 mr-1 text-green-500" />
                      Copied!
                    </>
                  ) : (
                    <>
                      <Copy className="h-4 w-4 mr-1" />
                      Copy Code
                    </>
                  )}
                </Button>
              )}
            </CardHeader>
            <ScrollArea className="h-[calc(100%-52px)]">
              {selectedFile ? (
                <pre className="p-4 text-sm font-mono bg-zinc-950 text-zinc-100 min-h-full">
                  <code>{selectedFile.content}</code>
                </pre>
              ) : (
                <div className="flex items-center justify-center h-full text-muted-foreground">
                  Select a file to view its contents
                </div>
              )}
            </ScrollArea>
          </Card>
        </div>

        {/* Instructions for AIDE */}
        <Card className="mt-6 border-amber-500/50">
          <CardHeader className="bg-amber-500/10">
            <CardTitle className="text-lg flex items-center gap-2">
              <Smartphone className="h-5 w-5 text-amber-500" />
              AIDE (Android IDE) Users - Manual Steps
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-4">
            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-amber-500/10 text-amber-500 flex items-center justify-center font-bold mb-2">1</div>
                <h3 className="font-medium mb-1">Create New Project</h3>
                <p className="text-sm text-muted-foreground">AIDE mein File &rarr; New Project &rarr; Gradle &rarr; com.kali.ai</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-amber-500/10 text-amber-500 flex items-center justify-center font-bold mb-2">2</div>
                <h3 className="font-medium mb-1">Copy Each File</h3>
                <p className="text-sm text-muted-foreground">File select karo, Copy button click karo, AIDE mein paste karo</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-amber-500/10 text-amber-500 flex items-center justify-center font-bold mb-2">3</div>
                <h3 className="font-medium mb-1">Same Path Use Karo</h3>
                <p className="text-sm text-muted-foreground">Jis path pe file hai wahi folder mein paste karo</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-amber-500/10 text-amber-500 flex items-center justify-center font-bold mb-2">4</div>
                <h3 className="font-medium mb-1">Build Karo</h3>
                <p className="text-sm text-muted-foreground">Sab files paste hone ke baad Run button press karo</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Instructions for Android Studio */}
        <Card className="mt-6">
          <CardHeader>
            <CardTitle className="text-lg">Android Studio Users</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-violet-500/10 text-violet-500 flex items-center justify-center font-bold mb-2">1</div>
                <h3 className="font-medium mb-1">Download ZIP</h3>
                <p className="text-sm text-muted-foreground">Click the Download button to get the complete project</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-violet-500/10 text-violet-500 flex items-center justify-center font-bold mb-2">2</div>
                <h3 className="font-medium mb-1">Extract Files</h3>
                <p className="text-sm text-muted-foreground">Unzip the downloaded file to your preferred location</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-violet-500/10 text-violet-500 flex items-center justify-center font-bold mb-2">3</div>
                <h3 className="font-medium mb-1">Open in Android Studio</h3>
                <p className="text-sm text-muted-foreground">File &rarr; Open &rarr; Select KaliAI folder</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50 border">
                <div className="h-8 w-8 rounded-full bg-violet-500/10 text-violet-500 flex items-center justify-center font-bold mb-2">4</div>
                <h3 className="font-medium mb-1">Build &amp; Run</h3>
                <p className="text-sm text-muted-foreground">Sync Gradle, then run on device or emulator</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
