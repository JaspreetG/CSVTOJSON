import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";

const FileUploader = () => {
  const [configFile, setConfigFile] = useState(null);
  const [csvFile, setCsvFile] = useState(null);

  // Mutation to handle file upload API call
  const uploadFilesMutation = useMutation({
    mutationFn: async (formData) => {
      try {
        const response = await axios.post(
          "http://localhost:8080/api/convert",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        return response.data;
      } catch (error) {
        console.error("Error uploading files:", error);
        throw new Error("File upload failed.");
      }
    },
    onError: (error) => {
      console.error("Error during mutation:", error);
      alert("Failed to upload files. Please try again.");
    },
    onSuccess: (data) => {
      console.log("Upload successful:", data);
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validate that both files are selected
    if (!configFile || !csvFile) {
      alert("Please upload both files.");
      return;
    }

    // Create FormData and append both files
    const formData = new FormData();
    formData.append("configFile", configFile);
    formData.append("csvFile", csvFile);

    console.log("Form data to be sent:", formData);
    uploadFilesMutation.mutate(formData);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="max-w-2xl w-full p-8 bg-white rounded-lg shadow-lg">
        <h2 className="mm text-3xl font-semibold text-center text-slate-800 mb-6">
          File Uploader
        </h2>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label
              className="block text-lg font-medium text-gray-700 mb-2"
              htmlFor="configFile"
            >
              Upload Config File (config.json)
            </label>
            <input
              type="file"
              id="configFile"
              accept=".json"
              onChange={(e) => setConfigFile(e.target.files[0])}
              className="block w-full border-2 border-gray-300 p-3 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label
              className="block text-lg font-medium text-gray-700 mb-2"
              htmlFor="csvFile"
            >
              Upload CSV File (sample.csv)
            </label>
            <input
              type="file"
              id="csvFile"
              accept=".csv"
              onChange={(e) => setCsvFile(e.target.files[0])}
              className="block w-full border-2 border-gray-300 p-3 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 px-6 bg-blue-600 text-white text-lg font-semibold rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
            disabled={uploadFilesMutation.isLoading}
          >
            {uploadFilesMutation.isLoading ? "Uploading..." : "Submit"}
          </button>
        </form>

        {/* Success Response Area */}
        {uploadFilesMutation.isSuccess && (
          <div className="mt-8 p-4 bg-green-50 border border-green-200 rounded-md">
            <h3 className="text-lg font-semibold text-green-800">Response:</h3>
            <pre className="mt-2 p-4 bg-white border border-gray-200 rounded-md text-sm">
              {JSON.stringify(uploadFilesMutation.data, null, 2)}
            </pre>
          </div>
        )}

        {/* Error Message Area */}
        {uploadFilesMutation.isError && (
          <div className="mt-8 p-4 bg-red-50 border border-red-200 rounded-md">
            <h3 className="text-lg font-semibold text-red-800">Error:</h3>
            <p className="mt-2 text-red-700">
              {uploadFilesMutation.error?.message}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default FileUploader;
